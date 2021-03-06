package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.utils.PermissionChecker;
import com.architjn.acjmusicplayer.utils.adapters.PlaylistListAdapter;
import com.architjn.acjmusicplayer.utils.decorations.SimpleDividerItemDecoration;
import com.architjn.acjmusicplayer.utils.handlers.PlaylistDBHelper;
import com.architjn.acjmusicplayer.utils.items.Playlist;

import java.util.ArrayList;

/**
 * Created by architjn on 27/11/15.
 */
public class PlaylistListFragment extends Fragment {

    private Context context;
    private View mainView;
    private RecyclerView rv;
    private PlaylistListAdapter adapter;
    private PermissionChecker permissionChecker;
    private PlaylistDBHelper playlistDBHelper;
    private View emptyView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist,
                container, false);
        context = view.getContext();
        mainView = view;
        init();
        return view;
    }

    private void init() {
        rv = (RecyclerView) mainView.findViewById(R.id.playlistContainer);
        emptyView = mainView.findViewById(R.id.playlist_empty_view);
        playlistDBHelper = new PlaylistDBHelper(context);
        checkPermissions();
        setHasOptionsMenu(true);
    }

    private void checkPermissions() {
        permissionChecker = new PermissionChecker(context, getActivity(), mainView);
        permissionChecker.check(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getResources().getString(R.string.storage_permission),
                new PermissionChecker.OnPermissionResponse() {
                    @Override
                    public void onAccepted() {
                        setArtistList();
                    }

                    @Override
                    public void onDecline() {
                        getActivity().finish();
                    }
                });
    }

    private void setArtistList() {
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.addItemDecoration(new SimpleDividerItemDecoration(context, 0));
        ArrayList<Playlist> artistsList = playlistDBHelper.getAllPlaylist();
        adapter = new PlaylistListAdapter(context, artistsList, this);
        rv.setAdapter(adapter);
        if (artistsList.size() == 0)
            listIsEmpty();
    }

    public void listIsEmpty() {
        emptyView.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
    }

    public void listNoMoreEmpty() {
        rv.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onBackPress() {
        adapter.onBackPressed();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.playlist_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void newPlaylistDialog() {
        new MaterialDialog.Builder(context)
                .title(R.string.new_playlist)
                .input(null, null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (!input.toString().matches("")) {
                            playlistDBHelper.createPlaylist(input.toString());
                            listNoMoreEmpty();
                            adapter.updateNewList(playlistDBHelper.getAllPlaylist());
                        }
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_playlist_fragment_add:
                newPlaylistDialog();
                return false;
            default:
                break;
        }

        return false;
    }


}
