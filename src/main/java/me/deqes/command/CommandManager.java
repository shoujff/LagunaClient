package me.deqes.command;

import lombok.Getter;
import me.deqes.Laguna;
import me.deqes.command.impl.BindCommand;
import me.deqes.command.impl.HelpCommand;


import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    @Getter
    List<Command> commands = new ArrayList<>();
    public CommandManager()
    {
        Laguna.getInstance().getEventBus().register(this);
        addCommands( new HelpCommand(), new BindCommand());
    }
    private void addCommands(Command... commands){
        for(Command command : commands){
            this.commands.add(command);
            Laguna.getInstance().getEventBus().register(command);
        }
    }

}
