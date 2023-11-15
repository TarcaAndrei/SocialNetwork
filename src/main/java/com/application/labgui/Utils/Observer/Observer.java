package com.application.labgui.Utils.Observer;

import com.application.labgui.Utils.Events.Event;

public interface Observer <E extends Event> {
    void update(E eventUpdate);
}
