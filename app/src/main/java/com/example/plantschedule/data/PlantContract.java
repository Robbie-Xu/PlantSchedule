package com.example.plantschedule.data;

import android.provider.BaseColumns;

public final class PlantContract {
    private void PetContract() {}

    public static final class PlantEntry implements BaseColumns {

        public final static String TABLE_NAME = "plants";

//        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PNAME = "name";
        public final static String COLUMN_PSNAME = "sname";
        public final static String COLUMN_PSPECIES = "species";
        public final static String COLUMN_PISMY = "ismy";
        public final static String COLUMN_PPICPATH = "picpath";
        public final static String COLUMN_PDESCRI = "descri";
        public final static String COLUMN_PLOC= "loc";

    }

    public static final class RecordEntry implements BaseColumns {

        public final static String TABLE_NAME = "records";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PNAME = "name";
        public final static String COLUMN_PDATE = "date";
        public final static String COLUMN_PISWATER = "iswater";
        public final static String COLUMN_PISFERTILIZE = "isferti";
        public final static String COLUMN_PISUSINGDRUG = "isdruging";
        public final static String COLUMN_PWEATHER = "weather";
        public final static String COLUMN_PLOC_LON = "lon";
        public final static String COLUMN_PLOC_LAT = "lat";
        public final static String COLUMN_PEVENTS_TITLE = "events_title";
        public final static String COLUMN_PEVENTS = "events";
        public final static String COLUMN_PLOC= "loc";
        public final static String COLUMN_PPATH= "picpath";

    }

}
