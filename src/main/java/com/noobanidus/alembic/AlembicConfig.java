package com.noobanidus.alembic;

import net.minecraftforge.common.config.Config;

@Config(modid = Alembic.MODID)
public class AlembicConfig {
    @Config.Comment("Specify number of seconds between checks for reearch")
    @Config.Name("Research Check Interval")
    @Config.RangeInt(min = 1)
    public static int interval = 9;

    @Config.Comment({"Set to false to disable automatic registration of research events", "Useful for reducing overhead when running Triumph, etc"})
    @Config.Name("Enable Events")
    public static boolean enable = true;
}
