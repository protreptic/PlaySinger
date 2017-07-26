package name.peterbukhal.android.playsinger.model.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RadioStation implements Parcelable {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String description;

    @DatabaseField
    private String genre;

    @DatabaseField
    private String country;

    @DatabaseField
    private String language;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source1;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source2;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source3;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source4;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source5;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String source6;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<RadioChannel> radioChannels;

    public RadioStation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSource1() {
        return source1;
    }

    public void setSource1(String url) {
        this.source1 = url;
    }

    public String getSource2() {
        return source2;
    }

    public void setSource2(String url) {
        this.source2 = url;
    }

    public String getSource3() {
        return source3;
    }

    public void setSource3(String url) {
        this.source3 = url;
    }

    public String getSource4() {
        return source4;
    }

    public void setSource4(String url) {
        this.source4 = url;
    }

    public String getSource5() {
        return source5;
    }

    public void setSource5(String url) {
        this.source5 = url;
    }

    public String getSource6() {
        return source6;
    }

    public void setSource6(String url) {
        this.source6 = url;
    }

    public ForeignCollection<RadioChannel> getRadioChannels() {
        return radioChannels;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof RadioStation)) return false;

        RadioStation station = (RadioStation) o;

        return (!name.equals(station.name) ||
                !description.equals(station.description) ||
                !genre.equals(station.genre) ||
                !country.equals(station.country) ||
                !language.equals(station.language) ||
                !source1.equals(station.source1)) ||
                !source2.equals(station.source2) ||
                !source3.equals(station.source3) ||
                !source4.equals(station.source4) ||
                !source5.equals(station.source5) ||
                !source6.equals(station.source6);
    }

    @Override
    public String toString() {
        return "RadioStation [name = \"" + name + "\", description = \"" + description + "\", genre = \"" + genre + "\", country = \"" + country + "\", language = \"" + language + "\", source1 = \"" + source1 + "\", source2 = \"" + source2 + "\", source3 = \"" + source3 + "\", source4 = \"" + source4 + "\", source5 = \"" + source5 + "\", source6 = \"" + source6 + "\" ]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(genre);
        dest.writeString(country);
        dest.writeString(language);
        dest.writeString(source1);
        dest.writeString(source2);
        dest.writeString(source3);
        dest.writeString(source4);
        dest.writeString(source5);
        dest.writeString(source6);
        //dest.writeStringList(radioChannels);
    }

    private RadioStation(Parcel parcel) {
        id = parcel.readLong();
        name = parcel.readString();
        description = parcel.readString();
        genre = parcel.readString();
        country = parcel.readString();
        language = parcel.readString();
        source1 = parcel.readString();
        source2 = parcel.readString();
        source3 = parcel.readString();
        source4 = parcel.readString();
        source5 = parcel.readString();
        source6 = parcel.readString();
        //parcel.readStringList(radioChannels);
    }

    public static final Parcelable.Creator<RadioStation> CREATOR = new Parcelable.Creator<RadioStation>() {

        public RadioStation createFromParcel(Parcel in) {
            return new RadioStation(in);
        }

        public RadioStation[] newArray(int size) {
            return new RadioStation[size];
        }

    };

}
