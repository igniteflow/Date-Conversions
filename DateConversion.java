import java.util.ArrayList;

public class DateConversion {

    public int[] leapYears = {1972, 1976, 1980, 1984, 1988, 1992,
        1996, 2000, 2004, 2008, 2012, 2016, 2020, 2024, 2028};
    public int[] earliestGregorian, latestGregorian;
    private int[] lowerBoundMonth, upperBoundMonth;
    public int earliestIndex, latestIndex;
    private int numQuarters;
    private double numYears;

    public static int convertToJulian(int year, int month, int day) {
        int date = (1461 * (year + 4800 + (month - 14) / 12)) / 4 + (367 * (month - 2 - 12 *
                ((month - 14) / 12))) / 12 -
                (3 * ((year + 4900 + (month - 14) / 12) / 100)) / 4 + day - 32075;

        return date;
    }

    /* Returns a Julian date as its Gregorian equivilant as an int[] {YEAR,MONTH,DAY} */
    public static int[] convertJulianToGregorian(int julianDate) {
        int l, n, i, j, d, m, y;
        l = julianDate + 68569;
        n = (4 * l) / 146097;
        l = l - (146097 * n + 3) / 4;
        i = (4000 * (l + 1)) / 1461001;
        l = l - (1461 * i) / 4 + 31;
        j = (80 * l) / 2447;
        d = l - (2447 * j) / 80;
        l = j / 11;
        m = j + 2 - (12 * l);
        y = 100 * (n - 49) + i + l;
        int[] gregorian = {y, m, d};

        return gregorian;
    }

    /* Returns a Gregorian date int array as a String eg. 04/01/2005 */
    public static String convertGregorianDateIntArrayToString(int[] dateArray) {
        String date;
        date = Integer.toString(dateArray[2]) + "/" +
                Integer.toString(dateArray[1]) + "/" +
                Integer.toString(dateArray[0]);

        return date;
    }

    /* Returns a Gregorian date int array as a String eg. Feb 2008 */
    public static String convertGregorianDateIntArrayToShortString(int[] dateArray) {
        String date;
        String[] months = {"", "Jan", "Feb", "March", "April", "May", "June", "July", "August", "Sept", "Oct", "Nov", "Dec"};
        date = months[dateArray[1]] + " " + Integer.toString(dateArray[0]);

        return date;
    }

    public int findWeeks(int[] dates) {
        int numWeeks = 0;

        // Get indices of earliest and latest julianDate
        earliestIndex = MinAndMaxFinder.findEarliestIndex(dates);
        latestIndex = MinAndMaxFinder.findLatestIndex(dates);
        float x = dates[earliestIndex] / 7;
        int y = Math.round(x);
        int z = y++;

        return numWeeks;
    }

    public int findMonths(int[] julianDate) {
        int[] bounds;

        // Get indices of earliest and latest julianDate
        earliestIndex = MinAndMaxFinder.findEarliestIndex(julianDate);
        latestIndex = MinAndMaxFinder.findLatestIndex(julianDate);

        earliestGregorian = convertJulianToGregorian(julianDate[earliestIndex]);
        latestGregorian = convertJulianToGregorian(julianDate[latestIndex]);

        //  Find the total number of months between the earliest and latest julianDate
        int numMonths = earliestToLatest(earliestGregorian, latestGregorian);

        /* Make two arrays: lowerBoundMonth and upperBoundMonth, both the same size as the numMonths
         * They will hold the bounds for each month in julian day format */
        setLowerBound(new int[numMonths]);
        setUpperBound(new int[numMonths]);

        /* bounds array:
         * [0]lowerBoundMonth(day number:greg),
         * [1]upperBoundMonth(day number:greg)*/
        bounds = boundFinderMonths(earliestGregorian);
        getLowerBound()[0] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[0]);
        getUpperBound()[0] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[1]);
        //test(earliestGregorian, bounds);

        int counter = 1;
        while ((earliestGregorian[0] != latestGregorian[0]) ||
                (earliestGregorian[1] != latestGregorian[1])) {
            /* Increment month(earliestgreg[1]) unless > 12
             * in which case increment year and set month to 1
             * continue until date equals latestDate    */
            if (earliestGregorian[1] <= 12) {
                earliestGregorian[1]++;
                bounds = boundFinderMonths(earliestGregorian);
                getLowerBound()[counter] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[0]);
                getUpperBound()[counter] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[1]);
            }  // end if
            if (earliestGregorian[1] == 13) {
                earliestGregorian[0]++;
                earliestGregorian[1] = 1;
                bounds = boundFinderMonths(earliestGregorian);
                getLowerBound()[counter] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[0]);
                getUpperBound()[counter] = convertToJulian(earliestGregorian[0], earliestGregorian[1], bounds[1]);
            }   // end if
            counter++;
        } // end while

        // Set bounds for the month of the latest transaction
        bounds = boundFinderMonths(latestGregorian);
        getLowerBound()[numMonths - 1] = convertToJulian(latestGregorian[0], latestGregorian[1], bounds[0]);
        getUpperBound()[numMonths - 1] = convertToJulian(latestGregorian[0], latestGregorian[1], bounds[1]);

        for (int i = 0; i < numMonths; i++) {
            int[] x = convertJulianToGregorian(getLowerBound()[i]);
            int[] y = convertJulianToGregorian(getUpperBound()[i]);
        }
        setLowerBound(lowerBoundMonth);

        return numMonths;
    } // end findMonths()

    /* Calculates number of quarters in the date range found in the dataset */
    public int findNumQuarters(int numMonths) {
        setNumQuarters(0);
        double months = numMonths;
        double x = months / 12;
        double numYears = Math.floor(x);
        double quartersA = numYears * 4;
        double y = (x - numYears) * 10;
        months = Math.ceil(y);
        double quartersB = 0;

        if (months > 0 & months < 4) {
            quartersB = 1;
        }
        if (months > 3 & months < 7) {
            quartersB = 2;
        }
        if (months > 6 & months < 10) {
            quartersB = 3;
        }
        if (months > 9 & months < 13) {
            quartersB = 4;
        }
        setNumQuarters((int) (quartersA + quartersB));

        return getNumQuarters();
    }

    public int[] quarterBoundsForGraph(int julianDate, int numQuarters) {
        int firstQuarter = 0;
        int[] date = convertJulianToGregorian(julianDate);
        int month = date[1];

        if (month < 4) {
            firstQuarter = 1;
        } else if (month >= 4 && month <= 6) {
            firstQuarter = 2;
        } else if (month >= 7 && month <= 9) {
            firstQuarter = 3;
        } else if (month >= 10 && month <= 12) {
            firstQuarter = 4;
        }

        int[] quarterBounds = new int[numQuarters];
        for (int i = 0; i < numQuarters; i++) {
            quarterBounds[i] = firstQuarter;
            if (firstQuarter == 4) {
                firstQuarter = 0;
            }
            firstQuarter++;
        }

        return quarterBounds;
    }

    /* Hack for quarter bounds */
    public int[] findQuarterBounds(int[] datesJulian, int numQuarters) {
        int numTransactions = datesJulian.length;
        int[] timeBlock = new int[numTransactions];
        int lowerBound = 0;
        int upperBound = 0;

        // Get indices of earliest and latest julianDate
        earliestIndex = MinAndMaxFinder.findEarliestIndex(datesJulian);
        latestIndex = MinAndMaxFinder.findLatestIndex(datesJulian);

        int[] year = new int[datesJulian.length];
        int[] month = new int[datesJulian.length];
        int[] dateGreg = new int[2];

        // Get the month and year values of all the transaction julianDate
        for (int i = 0; i < datesJulian.length; i++) {
            dateGreg = convertJulianToGregorian(datesJulian[i]);
            year[i] = dateGreg[0];
            month[i] = dateGreg[1];
        }

        /* Get the year and month value of the earliest date only
         * to be used as a starting point for the search */
        int y = year[earliestIndex];
        int m = month[earliestIndex];

        /* Find the initial lower and upper bounds for the search
         * loop */
        if (m > 0 & m < 4) {
            lowerBound = 0;
            upperBound = 4;
        }
        if (m > 3 & m < 7) {
            lowerBound = 3;
            upperBound = 7;
        }
        if (m > 6 & m < 10) {
            lowerBound = 6;
            upperBound = 10;
        }
        if (m > 9 & m < 13) {
            lowerBound = 9;
            upperBound = 13;
        }
        //System.out.println("Number of ... Quarters " + getNumQuarters());
        /* Search Loop */
        int groupCounter = 1;
        boolean[] indexToExcludeFromSearch = new boolean[numTransactions];
        java.util.Arrays.fill(indexToExcludeFromSearch, false);
        // Iterate for each quarter
        for (int i = 0; i < numQuarters; i++) {
            // Iterate for each transaction
            for (int j = 0; j < numTransactions; j++) {
                if (year[j] == y && (month[j] > lowerBound & month[j] < upperBound) &&
                        indexToExcludeFromSearch[j] == false) {
                    timeBlock[j] = groupCounter;
                    indexToExcludeFromSearch[j] = true;
                }
            } // end inner for
            if (lowerBound == 9 && upperBound == 13) {
                lowerBound = 0;
                upperBound = 4;
                y++;
            } else {
                lowerBound = lowerBound + 3;
                upperBound = upperBound + 3;
            }
            groupCounter++;
        } // end outer for
        groupCounter--;
        setNumQuarters(groupCounter);
        DataPreprocessing split = new DataPreprocessing();

        return timeBlock;
    } // end findMonths()

    

    public int[] findNumYears(int[] julianDate, int earliestIndex, int latestIndex) {
        int[] dateGreg = new int[2];
        int numTransactions = julianDate.length;
        int[] timeBlock = new int[numTransactions];
        int[] year = new int[numTransactions];
        int[] month = new int[numTransactions];
        // Get the month and year values of all the transaction julianDate
        for (int i = 0; i < numTransactions; i++) {
            dateGreg = convertJulianToGregorian(julianDate[i]);
            year[i] = dateGreg[0];
            month[i] = dateGreg[1];
        }

        /* Find number of years in data */
        int earliestYear = year[earliestIndex];
        int latestYear = year[latestIndex];
        setNumYears(latestYear - earliestYear);
        setNumYears(getNumYears() + 1);
        DataPreprocessing split = new DataPreprocessing();

        /* Search Loop */
        int groupCounter = 1;
        int yearCheck = earliestYear;
        boolean[] indexToExcludeFromSearch = new boolean[numTransactions];
        java.util.Arrays.fill(indexToExcludeFromSearch, false);

        // Iterate for each year
        for (int i = 0; i < getNumYears(); i++) {
            // Iterate for each transaction
            for (int j = 0; j < numTransactions; j++) {
                if (year[j] == yearCheck &&
                        indexToExcludeFromSearch[j] == false) {
                    timeBlock[j] = groupCounter;
                    indexToExcludeFromSearch[j] = true;
                }
            } // end inner for
            yearCheck++;
            groupCounter++;
        } // end outer for
        return timeBlock;
    }

    public int[] boundFinderMonths(int[] date) {
        int[] bound = {0, 0};
        int year, month, day;
        year = date[0];
        month = date[1];
        day = date[2];

        /***  LOWER BOUND = bound[0]***/
        bound[0] = 1;

        /*** UPPER BOUND ***/
        boolean leapYearFlag = false;
        for (int i = 0; i < leapYears.length; i++) {
            if (year == leapYears[i]) {
                leapYearFlag = true;
            }
        }

        // If leap year and month is Feb then set upperBoundMonth to 29
        if (leapYearFlag && month == 2) {
            bound[1] = 29;
        } else {
            bound[1] = calculateUpperBound(month);
        }
        return bound;
    }

    /* Takes gregorian date array */
    public int[] boundFinderQuarters(int[] date) {
        int[] bound = {0, 0};
        int year, month, day;
        year = date[0];
        month = date[1];
        day = date[2];

        /***  LOWER BOUND = bound[0]***/
        bound[0] = 1;

        /*** UPPER BOUND ***/
        boolean leapYearFlag = false;
        for (int i = 0; i < leapYears.length; i++) {
            if (year == leapYears[i]) {
                leapYearFlag = true;
            }
        }

        // If leap year and month is Feb then set upperBoundMonth to 29
        if (leapYearFlag && month == 2) {
            bound[1] = 29;
        } else {
            bound[1] = calculateUpperBound(month);
        }
        return bound;
    }

    public int earliestToLatest(int[] earliestGregorian, int[] latestGregorian) {
        /* Gregorian integer array structure:  [0] = year | [1] = month | [2] = day */
        int numMonths;
        // Earliest(e) info
        int eYear, eMonth, eDay;
        eYear = earliestGregorian[0];
        eMonth = earliestGregorian[1];
        eDay = earliestGregorian[2];
        // Latest(l) info
        int lYear, lMonth, lDay;
        lYear = latestGregorian[0];
        lMonth = latestGregorian[1];
        lDay = latestGregorian[2];

        /* If earliest and latest julianDate occur in the same year
         * subtract latest from earliest and increment */
        if (eYear == lYear) {
            numMonths = lMonth - eMonth;
            numMonths++;
        } else {
            // Number of months in earliest year
            int x = 12 - eMonth;
            x++;
            // Number of months in latest year
            int y = lMonth;
            int z = 0;
            /* If the latest date is the following year from the earliest,
             * then the calculations are already completed, otherwise
             * multiply the number of years inbetween by 12 and add to total*/
            if (lYear != ++eYear) {
                // Number of months between
                z = lYear - eYear;
                z = z * 12;
            }
            numMonths = x + y + z;
        }
        return numMonths;
    }

    public int calculateUpperBound(int month) {
        int upperBound = 0;
        // If month is April, June, Sept or Nov
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            upperBound = 30;
        } // If Feburary
        else if (month == 2) {
            upperBound = 28;
        } else {
            upperBound = 31;
        }
        return upperBound;
    }

    public static void main(String[] args) {
        //DateConversion date = new DateConversion();
        //date.findMonths();
    }  // end main

    /**
     * @return the lowerBoundMonth
     */
    public int[] getLowerBound() {
        return lowerBoundMonth;
    }

    /**
     * @param lowerBoundMonth the lowerBoundMonth to set
     */
    public void setLowerBound(int[] lowerBound) {
        this.lowerBoundMonth = lowerBound;
    }

    /**
     * @return the upperBoundMonth
     */
    public int[] getUpperBound() {
        return upperBoundMonth;
    }

    /**
     * @param upperBoundMonth the upperBoundMonth to set
     */
    public void setUpperBound(int[] upperBound) {
        this.upperBoundMonth = upperBound;
    }

    /**
     * @return the numQuarters
     */
    public int getNumQuarters() {
        return numQuarters;
    }

    /**
     * @param numQuarters the numQuarters to set
     */
    public void setNumQuarters(int numQuarters) {
        this.numQuarters = numQuarters;
    }

    /**
     * @return the numYears
     */
    public double getNumYears() {
        return numYears;
    }

    /**
     * @param numYears the numYears to set
     */
    public void setNumYears(double numYears) {
        this.numYears = numYears;
    }
}  // end class

