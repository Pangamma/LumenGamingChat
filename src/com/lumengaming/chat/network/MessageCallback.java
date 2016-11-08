package com.lumengaming.chat.network;

import java.io.IOException;

/**
 *
 * @author Taylor
 */
public interface MessageCallback {
	public void onMessage(String message) throws IOException;
}
