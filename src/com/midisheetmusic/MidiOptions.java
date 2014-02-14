/*
- * Copyright (c) 2007-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */


package com.midisheetmusic;

import java.io.*;
import android.util.Log;
import org.json.*;
import android.graphics.*;

/** @class MidiOptions
 * The MidiOptions class contains the available options for
 * modifying the sheet music and sound.  These options are collected
 * from the SettingsActivity, and are passed to the SheetMusic and
 * MidiPlayer classes.
 */
public class MidiOptions implements Serializable {

    // The possible values for showNoteLetters
    public static final int NoteNameNone           = 0;
    public static final int NoteNameLetter         = 1;
    public static final int NoteNameFixedDoReMi    = 2;
    public static final int NoteNameMovableDoReMi  = 3;
    public static final int NoteNameFixedNumber    = 4;
    public static final int NoteNameMovableNumber  = 5;

    
}


