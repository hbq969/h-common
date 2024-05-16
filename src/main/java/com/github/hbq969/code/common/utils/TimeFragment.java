package com.github.hbq969.code.common.utils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 时间分段器
 * @createTime : 11:58:23, 2023.03.31, 周五
 */
public enum TimeFragment {

    /**
     * [年)
     */
    YEARS {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end = new DateTime(start * 1000L)
                        .plusYears(1)
                        .monthOfYear().setCopy(1)
                        .dayOfMonth().setCopy(1)
                        .hourOfDay().setCopy(0)
                        .minuteOfHour().setCopy(0)
                        .secondOfMinute().setCopy(0)
                        .getMillis() / 1000L;
                RoundInfo rdi = new RoundInfo().setStartTime(
                        new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end).format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }
    },

    /**
     * [年]
     */
    YEARS_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end = new DateTime(start * 1000L)
                        .plusYears(1)
                        .monthOfYear().setCopy(1)
                        .dayOfMonth().setCopy(1)
                        .hourOfDay().setCopy(0)
                        .minuteOfHour().setCopy(0)
                        .secondOfMinute().setCopy(0)
                        .getMillis()
                        / 1000L;
                RoundInfo rdi =
                        new RoundInfo().setStartTime(new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end - 1).closure().format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }
    },

    /**
     * [月)
     */
    MONTHS {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end = new DateTime(start * 1000L)
                        .plusMonths(1)
                        .dayOfMonth().setCopy(1)
                        .hourOfDay().setCopy(0)
                        .minuteOfHour().setCopy(0)
                        .secondOfMinute().setCopy(0)
                        .getMillis() / 1000L;
                RoundInfo rdi =
                        new RoundInfo().setStartTime(new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end).format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }
    },

    /**
     * [月]
     */
    MONTHS_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end = new DateTime(start * 1000L)
                        .plusMonths(1)
                        .dayOfMonth().setCopy(1)
                        .hourOfDay().setCopy(0)
                        .minuteOfHour().setCopy(0)
                        .secondOfMinute().setCopy(0)
                        .getMillis() / 1000L;
                RoundInfo rdi = new RoundInfo().setStartTime(
                        new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end - 1).closure().format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }
    },

    /**
     * [周,星期一为第一天]
     */
    WEEKS_ENDCLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end =
                        new DateTime(start * 1000L).plusWeeks(1).dayOfWeek().setCopy(1).hourOfDay().setCopy(0)
                                .minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).getMillis() / 1000L;
                RoundInfo rdi = new RoundInfo().setStartTime(
                        new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end - 1).closure().format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }

    },

    /**
     * [周,星期一为第一天)
     */
    WEEKS {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long start = startTimeSecs;
            while (start <= endTimeSecs) {
                long end =
                        new DateTime(start * 1000L).plusWeeks(1).dayOfWeek().setCopy(1).hourOfDay().setCopy(0)
                                .minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).getMillis() / 1000L;
                RoundInfo rdi = new RoundInfo().setStartTime(
                        new TimeInfo().setSecs(start).format());
                if (end >= endTimeSecs) {
                    rdi.setEndTime(new TimeInfo().setSecs(endTimeSecs).format());
                    roundInfos.add(rdi);
                    break;
                } else {
                    rdi.setEndTime(new TimeInfo().setSecs(end).format());
                    roundInfos.add(rdi);
                    start = end;
                }
            }
            return roundInfos;
        }
    },

    /**
     * [天)
     */
    DAYS {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            List<Segment> segments = DigitSplit.timeStep(
                            DAY_TIME_SECS)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment segment : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(segment.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(segment.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [天]
     */
    DAYS_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            List<Segment> segments = DigitSplit.timeStep(
                            DAY_TIME_SECS)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment segment : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(segment.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(segment.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [小时)
     */
    HOURS {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            List<Segment> segments = DigitSplit.timeStep(
                            HOUR_TIME_SECS)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [小时]
     */
    HOURS_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            List<Segment> segments = DigitSplit.timeStep(
                            HOUR_TIME_SECS)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟)
     */
    MINUTES {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟]
     */
    MINUTES_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟)
     */
    MINUTES5 {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 5;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟]
     */
    MINUTES5_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 5;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟)
     */
    MINUTES10 {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 10;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟]
     */
    MINUTES10_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 10;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟)
     */
    MINUTES30 {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 30;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd()).format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    },

    /**
     * [分钟]
     */
    MINUTES30_CLOSURE {
        @Override
        public List<RoundInfo> fragment(String startTime,
                                        String endTime, FormatTime formatTime) {
            return fragment(formatTime.toSecs(startTime), formatTime.toSecs(endTime), TimeUnit.SECONDS);
        }

        @Override
        public List<RoundInfo> fragment(long startTime,
                                        long endTime, TimeUnit unit) {
            List<RoundInfo> roundInfos = new ArrayList<>();
            long startTimeSecs = TimeUnit.SECONDS.convert(
                    startTime, unit);
            long endTimeSecs = TimeUnit.SECONDS.convert(
                    endTime, unit);
            long stepSecs = MINUTE_TIME_SECS * 30;
            List<Segment> segments = DigitSplit.timeStep(
                            stepSecs)
                    .split(startTimeSecs, endTimeSecs);
            if (segments != null) {
                for (Segment seg : segments) {
                    RoundInfo roundInfo = new RoundInfo()
                            .setStartTime(new TimeInfo().setSecs(seg.getBegin()).format())
                            .setEndTime(new TimeInfo().setSecs(seg.getEnd() - 1).closure().format());
                    roundInfos.add(roundInfo);
                }
            }
            return roundInfos;
        }
    };

    /**
     * 对时间进行分段
     *
     * @param startTime 开始时间数值
     * @param endTime   结束时间数值
     * @param unit      时间单位
     * @return
     */
    public List<RoundInfo> fragment(long startTime,
                                    long endTime, TimeUnit unit) {
        throw new AbstractMethodError();
    }

    /**
     * 对时间进行分段
     *
     * @param startTime  开始时间字符串
     * @param endTime    结束时间字符串
     * @param formatTime 时间格式化模板
     * @return
     */
    public List<RoundInfo> fragment(String startTime,
                                    String endTime, FormatTime formatTime) {
        throw new AbstractMethodError();
    }

    private final static long MINUTE_TIME_SECS = 60L;
    private final static long HOUR_TIME_SECS = 60 * MINUTE_TIME_SECS;
    public final static long DAY_TIME_SECS = 24 * HOUR_TIME_SECS;

}
