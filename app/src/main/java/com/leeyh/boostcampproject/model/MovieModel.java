package com.leeyh.boostcampproject.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.leeyh.boostcampproject.BR;

public class MovieModel extends BaseObservable {

    @Bindable
    private String title;
    @Bindable
    private String link;
    @Bindable
    private String image;
    @Bindable
    private String subtitle;
    @Bindable
    private String pubDate;
    @Bindable
    private String director;
    @Bindable
    private String actor;
    @Bindable
    private float userRating;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
        notifyPropertyChanged(BR.link);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        notifyPropertyChanged(BR.subtitle);
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
        notifyPropertyChanged(BR.pubDate);
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
        notifyPropertyChanged(BR.director);
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
        notifyPropertyChanged(BR.actor);
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
        notifyPropertyChanged(BR.userRating);
    }

    //to compare old recyclerView list and new list, override equals.
    @Override
    public boolean equals(Object obj) {
        boolean same = false;
        if (obj instanceof MovieModel) {
            same = (this.actor.equals(((MovieModel) obj).actor)) && (this.director.equals(((MovieModel) obj).director))
                    && (this.image.equals(((MovieModel) obj).image)) && this.link.equals(((MovieModel) obj).link)
                    && (this.pubDate.equals(((MovieModel) obj).pubDate)) && (this.subtitle.equals(((MovieModel) obj).subtitle))
                    && (this.userRating == ((MovieModel) obj).userRating) && (this.title.equals(((MovieModel) obj).title));
        }
        return same;
    }
}
