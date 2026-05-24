package me.deqes.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class EventMouse
{
    int key, action;
    double mouseX, mouseY;
}
