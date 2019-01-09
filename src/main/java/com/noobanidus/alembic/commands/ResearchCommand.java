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
import java.util.stream.Collectors;
import java.util.function.Predicate;

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
        return "/alembic category | category <option search> | search <term> | <research>";
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
        if (args.length > 2 || args.length == 0) {
            throw new WrongUsageException(getUsage(sender));
        }

        if (args.length == 2) {
            String t = args[1].trim().toLowerCase();

            StringJoiner matches = new StringJoiner(", ", TextFormatting.GOLD + "[" + TextFormatting.WHITE, TextFormatting.GOLD + "]");

            Predicate<String> filter = (k) -> k != null && (k.trim().toLowerCase().contains(t) || t.contains(k.trim().toLowerCase()));

            if (args[0].trim().equalsIgnoreCase("search")) {
                List<ResearchEntry> possibleEntries = new ArrayList<>();

                ResearchCategories.researchCategories.values().forEach((cat) -> {
                    List<String> poss = cat.research.keySet().stream().filter(filter).collect(Collectors.toList());
                    if (poss.size() != 0) poss.forEach((k) -> possibleEntries.add(cat.research.get(k)));
                });

                if (possibleEntries.size() != 0) {
                    possibleEntries.forEach((entry) -> {
                        for (int i = 0; i < entry.getStages().length; i++) {
                            matches.add(String.format("%s@%d", entry.getKey(), i+1));
                        }
                    });
                }

                if (matches.length() != 0) {
                    sender.sendMessage(new TextComponentString(String.format("Found possible results %s.", matches.toString())));
                }
            } else if (args[0].trim().equalsIgnoreCase("category") || args[0].trim().equalsIgnoreCase("cat")) {
                List<String> possibleCategories = ResearchCategories.researchCategories.keySet().stream().filter(filter).collect(Collectors.toList());

                if (possibleCategories.size() != 0) {
                    possibleCategories.forEach((key) -> {
                        ResearchCategory cat = ResearchCategories.getResearchCategory(key);
                        cat.research.values().forEach((val) -> {
                            for (int i = 0; i < val.getStages().length; i++) {
                                matches.add(String.format("%s@%d", val.getKey(), i+1));
                            }
                        });
                    });
                }

                if (matches.length() != 0) {
                    StringJoiner matches2 = new StringJoiner(", ", TextFormatting.GOLD + "[" + TextFormatting.WHITE, TextFormatting.GOLD + "]");
                    possibleCategories.forEach(matches2::add);
                    sender.sendMessage(new TextComponentString(String.format("Found valid categories %s.", matches2.toString())));
                    sender.sendMessage(new TextComponentString(String.format("Research entries from those categories %s", matches.toString())));
                }
            }
        } else if (args[0].trim().equalsIgnoreCase("cat") || args[0].trim().equalsIgnoreCase("category")) {
            StringJoiner cats = new StringJoiner(", ", "[", "]");
            ResearchCategories.researchCategories.keySet().forEach(cats::add);
            sender.sendMessage(new TextComponentString(String.format("Research categories %s.", cats.toString())));
        } else {
            String t = args[0].trim();

            ResearchCategories.researchCategories.forEach((catName, cat) -> {
                cat.research.values().forEach((entry) -> {
                    if (entry.getKey().equalsIgnoreCase(t)) {
                        StringJoiner stages = new StringJoiner(", ", "[", "]");
                        for (int i = 0; i < entry.getStages().length; i++) {
                            stages.add(String.format("alembic:%s/%s@%d", catName.toLowerCase(), entry.getKey().toLowerCase(), i+1));
                        }
                        sender.sendMessage(new TextComponentString(String.format("Found matching research [%s] with keys %s.", entry.getKey(), stages.toString())));
                    }
                });
            });
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
