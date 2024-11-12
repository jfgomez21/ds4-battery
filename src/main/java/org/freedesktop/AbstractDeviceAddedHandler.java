package org.freedesktop;

import org.freedesktop.dbus.handlers.AbstractSignalHandlerBase;

public abstract class AbstractDeviceAddedHandler extends AbstractSignalHandlerBase<UPower.DeviceAdded> {
	@Override
	public final Class<UPower.DeviceAdded> getImplementationClass(){
		return UPower.DeviceAdded.class;
	}
}
