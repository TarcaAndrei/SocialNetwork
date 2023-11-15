package com.application.labgui.Utils.Events;

import com.application.labgui.Domain.Utilizator;

public class ServiceChangeEvent implements Event {
    private ChangeEventType type;
    private Utilizator data, oldData;
}
