package com.justinschultz.puppyspotting;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class main extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, fetchPuppies()));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, open a browser with the puppy link
				String puppyImageLink = getPuppyImageLink(((TextView) view).getText().toString());
				openBrowser(puppyImageLink);
			}
		});
	}
	
	// Force a refresh for now...
	@Override
	public void onStop() {
		System.exit(0);
	}
	
	public static List<String> fetchPuppies() {
		// Singleton pattern.
		Twitter twitter = new TwitterFactory().getInstance();
		List<String> puppyArray = new ArrayList<String>();

		try {
			List<Tweet> tweets = null;
			
			// TODO: Make query configurable
			QueryResult result = twitter.search(new Query("puppy+twitpic"));
			tweets = result.getTweets();

			for (Tweet tweet : tweets) {
				puppyArray.add("@" + tweet.getFromUser() + " - "
						+ tweet.getText());
			}

			// JMS: Commented out, because apparently this works
			// in Android too.
			// System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			 System.out.println("Failed to search tweets: " +
			 te.getMessage());
		}

		return puppyArray;
	}

	// TODO: Move to utility class
	private String getPuppyImageLink(String text) {
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
		String urlStr = "";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		while (m.find()) {
			urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}
		}
		
		return urlStr;
	}
	
	// TODO: Move to utility class
	private void openBrowser(String url) {
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
}