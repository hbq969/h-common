package com.github.hbq969.code.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description :
 * @createTime : 2023/12/13 16:54
 */
public enum TimeUnitHandler {
    NANOSECONDS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.NANOSECONDS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing one thousandth of a millisecond
     */
    MICROSECONDS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.MICROSECONDS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing one thousandth of a second
     */
    MILLISECONDS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.MILLISECONDS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing one second
     */
    SECONDS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing sixty seconds
     */
    MINUTES {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.MINUTES.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing sixty minutes
     */
    HOURS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.HOURS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    },

    /**
     * Time unit representing twenty four hours
     */
    DAYS {
        @Override
        public TimeUnitHandler sleep(long t) {
            try {
                TimeUnit.DAYS.sleep(t);
            } catch (InterruptedException e) {
            }
            return this;
        }
    };

    public TimeUnitHandler sleep(long t) {
        throw new AbstractMethodError();
    }

    public void handle(Runnable r) {
        r.run();
    }
}
