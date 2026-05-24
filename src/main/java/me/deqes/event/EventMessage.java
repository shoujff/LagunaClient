package me.deqes.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventMessage {

    String message;
    boolean isCmd;

}
