package me.deqes.command;

import lombok.Getter;

public class Command {
    @Getter
    String alias;
    @Getter
    String desc;
    public static final String prefix = ".";
    public Command(String alias, String desc){
        this.alias = alias;
        this.desc = desc;
    }
}
