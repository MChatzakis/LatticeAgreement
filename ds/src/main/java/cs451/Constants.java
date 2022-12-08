package cs451;

public class Constants {
    public static final int ARG_LIMIT_CONFIG = 7;

    // indexes for id
    public static final int ID_KEY = 0;
    public static final int ID_VALUE = 1;

    // indexes for hosts
    public static final int HOSTS_KEY = 2;
    public static final int HOSTS_VALUE = 3;

    // indexes for output
    public static final int OUTPUT_KEY = 4;
    public static final int OUTPUT_VALUE = 5;

    // indexes for config
    public static final int CONFIG_VALUE = 6;
    public static final int MAX_PACKET_SIZE = 65535;//2*256;
    public static final long RETRANSMISSION_DELAY = 5000L;
    public static final boolean UDP_MESSAGING_VERBOSE = false;
    public static final boolean FLL_MESSAGING_VERBOSE = false;
    public static final boolean SBL_MESSAGING_VERBOSE = false;
    public static final boolean PL_MESSAGING_VERBOSE = false;
    public static final boolean PROCESS_MESSAGING_VERBOSE = false;
    public static final boolean PROCESS_BROADCASTING_VERBOSE = false;
    public static final boolean BEB_MESSAGING_VERBOSE = false;
    public static final int MESSAGES_PER_BATCH = 8;
}
