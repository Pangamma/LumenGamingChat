package com.lumengaming.chat.asyncnetwork;

import java.util.ArrayList;

public interface AsyncCallback<T> {
	public void doCallback(T t);
}
