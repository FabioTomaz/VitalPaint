package com.icm.projeto.vitalpaint.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the users database.
 */
public class UserContract {


    public static final class UserEntry implements BaseColumns {

        public static final String TABLE_NAME = "user";

        public static final String COLUMN_USER_ID = "user_id"; //primary key
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_N_MATCHES = "n_matches";
        public static final String COLUMN_N_WINS = "n_wins";
        public static final String COLUMN_N_HITS = "short_desc"; //number of hits to ohter players in all matches
        public static final String COLUMN_KILL_DEATH_RATIO = "kill_death_ratio";
    }

    /* Inner class that defines the contents of o table of the user's last locations he played */
    public static final class UserStatisticsEntry implements BaseColumns {

        public static final String TABLE_NAME = "user_locations";


        public static final String COLUMN_USER = "user"; //foreign key
        public static final String COLUMN_LOCATIONS = "locations_played";
    }
}