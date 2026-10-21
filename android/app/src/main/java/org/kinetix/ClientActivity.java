package org.kinetix;

import android.os.Bundle;
import org.libsdl.app.SDLActivity;

public class ClientActivity extends SDLActivity {
    private static final int COMMAND_RESTART_APP = 0x8001;

    @Override
    protected void onCreate(Bundle b) { super.onCreate(b); }

    @Override
    protected String[] getLibraries() {
        return new String[]{ "SDL2", "main" };
    }

    public void startServer(String[] args) { ServerService.start(this, args); }
    public void executeCommand(String cmd)  { ServerService.enqueue(cmd); }
    public boolean isServerRunning()        { return ServerService.isRunning(); }
}
