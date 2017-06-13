package com.suraj.musicmanagement.interfaces;

import com.suraj.musicmanagement.data.Song;

/**
 * Created by suraj on 13/6/17.
 */
public interface SongEditedCallBack {
    void songEdited(Song original, Song edited);
}
