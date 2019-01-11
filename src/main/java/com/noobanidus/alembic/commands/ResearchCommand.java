package com.noobanidus.alembic.commands;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.*;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ResearchCommand extends CommandBase {
    @Override
    public String getName() {
        return "research";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.research.usage";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("research");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String t = args[0].trim();

        Function<ResearchEntry, ITextComponent> stageResolver = (k) -> new TextComponentString("");

        if (sender instanceof EntityPlayer) {
            stageResolver = (research) -> {
                StringJoiner stages = new StringJoiner(", ", "[", "]");

                for (int i = 0; i < research.getStages().length; i++) {
                    if (ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer) sender, String.format("%s@%d", research.getKey(), i + 1))) {
                        stages.add(String.format("@%d", i + 1));
                    }
                }

                if (stages.length() == 2) return new TextComponentString("");

                ITextComponent stageValues = new TextComponentString(stages.toString()).setStyle(new Style().setColor(TextFormatting.GOLD));

                return new TextComponentTranslation("command.research.stages", stageValues);
            };
        }

        boolean resultFound = false;

        sender.sendMessage(new TextComponentTranslation("command.research.scanning"));

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
                    ITextComponent entryName = new TextComponentString(entry.getLocalizedName()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
                    ITextComponent keys = new TextComponentString(stages.toString()).setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE));
                    ITextComponent stage = stageResolver.apply(entry);
                    sender.sendMessage(new TextComponentTranslation("command.research.matching", entryName, keys, stage));
                    resultFound = true;
                }
            }
        }

        if (!resultFound) {
            sender.sendMessage(new TextComponentTranslation("command.research.noresults", t));
        }
    }
}
