package com.github.hbq969.code.common.utils;

import java.util.concurrent.CompletableFuture;
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.NANOSECONDS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.NANOSECONDS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.MICROSECONDS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.MICROSECONDS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.MINUTES.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.MINUTES.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.HOURS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.HOURS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
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

        @Override
        public void asyncAwaitAndRun(Runnable r, long time) {
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.DAYS.sleep(time);
                } catch (InterruptedException e) {
                    return;
                }
                r.run();
            });
        }

        @Override
        public void syncAwaitAndRun(Runnable r, long time) {
            try {
                TimeUnit.DAYS.sleep(time);
            } catch (InterruptedException e) {
                return;
            }
            r.run();
        }
    };

    public TimeUnitHandler sleep(long t) {
        throw new AbstractMethodError();
    }

    public void handle(Runnable r) {
        r.run();
    }

    public void syncAwaitAndRun(Runnable r,long time){
        throw new AbstractMethodError();
    }

    public void asyncAwaitAndRun(Runnable r,long time){
        throw new AbstractMethodError();
    }
}
