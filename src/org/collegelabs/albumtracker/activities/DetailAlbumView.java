package org.collegelabs.albumtracker.activities;

import org.collegelabs.albumtracker.Constants;
import org.collegelabs.albumtracker.R;
import org.collegelabs.albumtracker.content.AlbumProvider;
import org.collegelabs.albumtracker.fragments.ArtworkFragment;
import org.collegelabs.albumtracker.fragments.TrackListFragment;
import org.collegelabs.albumtracker.structures.Album;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DetailAlbumView extends BaseActivity {

	private Album album;
	private ViewPager  mViewPager;
	private TabsAdapter mTabsAdapter;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.activity_album_detail);

		album = (Album) getIntent().getExtras().getParcelable("album");


		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayShowHomeEnabled(false);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
		mViewPager = (ViewPager)findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), actionbar, mViewPager, album);
        
        mTabsAdapter.addTab(actionbar.newTab().setText("Info"));
        mTabsAdapter.addTab(actionbar.newTab().setText("Tracks"));
  
		
		//Moved from Album Grid because of UI glitches	
		if(album.isNew){
			new AsyncTask<Void,Void,Void>(){
				@Override protected Void doInBackground(Void... params) {
					String where = AlbumProvider.Album.Albums.ALBUM_ID+" = ?";
					String[] selectionArgs = {""+album.ID};
					ContentValues values = new ContentValues();
					values.put(AlbumProvider.Album.Albums.ALBUM_NEW, 0); 
					getContentResolver().update(AlbumProvider.Album.Albums.CONTENT_URI, values, where, selectionArgs);
					return null;
				}
			}.execute();
		}
	}

	public void onClick(View v){
		switch(v.getId()){
		
		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, Constants.MENU_STARRED, 0, "Star")
			.setIcon(album.isStarred ? R.drawable.star_on : R.drawable.star_off)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case Constants.MENU_STARRED:
			album.isStarred = !album.isStarred;
			if(album.isStarred){
				Toast.makeText(this, "Starred", Toast.LENGTH_SHORT).show();
			}else{				
				Toast.makeText(this, "Removed from Starred", Toast.LENGTH_SHORT).show();
			}
			
			new AsyncTask<Void,Void,Void>(){
				@Override protected Void doInBackground(Void... params) {
					String where = AlbumProvider.Album.Albums.ALBUM_ID+" = ?";
					String[] selectionArgs = {""+album.ID};
					ContentValues values = new ContentValues();
					values.put(AlbumProvider.Album.Albums.ALBUM_STARRED, album.isStarred ? 1 : 0);
					getContentResolver().update(AlbumProvider.Album.Albums.CONTENT_URI, values, where, selectionArgs);
					
					return null;
				}
			}.execute();
			
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	private static class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener {
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private int tabsCount = 0;
        private Album mAlbum;
        
        public TabsAdapter(FragmentManager fragmentManager, ActionBar actionBar, ViewPager pager, Album album) {
            super(fragmentManager);
            mActionBar = actionBar;
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
            mAlbum = album;
        }

        public void addTab(ActionBar.Tab tab) {
        	tabsCount++;
            mActionBar.addTab(tab.setTabListener(this));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return tabsCount;
        }

        @Override
        public Fragment getItem(int position) {
        	Bundle args = new Bundle();
        	args.putParcelable("album", mAlbum);
        	Fragment f;
        	
        	switch(position){
        	case 0:
        		f = new ArtworkFragment();
        		break;
        	case 1:
        		f = new TrackListFragment();
        		break;
        	default:
        		throw new IllegalArgumentException("invalid position");
        	}
        	
        	f.setArguments(args);
        	return f;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			mViewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			
		}
    }

}
