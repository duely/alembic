package com.noobanidus.alembic.commands;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SideOnly(Side.CLIENT)
public class ResearchCommand extends CommandBase implements IClientCommand {
    @Override
    @Nonnull
    public String getName() {
        return "alembic";
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public String getUsage(ICommandSender sender) {
        return "/alembic <term>";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return Lists.newArrayList("alembic");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute (MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String t = args[0].trim();

        boolean resultFound = false;

        sender.sendMessage(new TextComponentString("--- --- Searching for research --- ---"));

        for (Map.Entry<String, ResearchCategory> e : ResearchCategories.researchCategories.entrySet()) {
            String catName = e.getKey();
            ResearchCategory cat = e.getValue();
            for (ResearchEntry entry : cat.research.values()) {
                String k = entry.getKey().trim().toLowerCase();

                if (k.contains(t) || t.contains(k)) {
                    StringJoiner stages = new StringJoiner(", ", "[", "]");
                    for (int i = 0; i < entry.getStages().length; i++) {
                        stages.add(String.format("alembic:%s/%s@%d", catName.toLowerCase(), entry.getKey().toLowerCase(), i + 1));
                    }
                    sender.sendMessage(new TextComponentString(String.format("Found matching %s%s%s, keys: %s%s.", TextFormatting.LIGHT_PURPLE, entry.getLocalizedName(), TextFormatting.WHITE, TextFormatting.LIGHT_PURPLE, stages.toString())));
                    resultFound = true;
                }
            }
        }

        if (!resultFound) {
            sender.sendMessage(new TextComponentString(String.format("No results found for Thaumcraft research using term [%s]", t)));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "search", "category"): Collections.emptyList();
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

}
