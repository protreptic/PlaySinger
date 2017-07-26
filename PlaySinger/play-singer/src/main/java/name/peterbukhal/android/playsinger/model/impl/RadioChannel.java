package name.peterbukhal.android.playsinger.model.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RadioChannel implements Parcelable {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private RadioStation radioStation;

    @DatabaseField
    private String title;

    @DatabaseField
    private String uri;

    public RadioChannel() {

    }

    private RadioChannel(Parcel parcel) {
        id = parcel.readLong();
        title = parcel.readString();
        uri = parcel.readString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static final Creator<RadioChannel> CREATOR = new Creator<RadioChannel>() {

        @Override
        public RadioChannel createFromParcel(Parcel in) {
            return new RadioChannel(in);
        }

        @Override
        public RadioChannel[] newArray(int size) {
            return new RadioChannel[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(uri);
    }

    public RadioStation getRadioStation() {
        return radioStation;
    }

    public void setRadioStation(RadioStation radioStation) {
        this.radioStation = radioStation;
    }
}
