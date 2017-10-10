package edu.orangecoastcollege.cs273.flagquiz;


public class Country {

    private String mName;
    private String mRegion;
    private String mFileName;

    public Country(String name, String region) {
        mName = name;
        mRegion = region;
        name = name.replaceAll(" ", "_");
        region = region.replaceAll(" ", "_");
        mFileName = region + "/" + region + "-" + name + ".png";
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRegion() {
        return mRegion;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (!mName.equals(country.mName)) return false;
        if (!mRegion.equals(country.mRegion)) return false;
        return mFileName.equals(country.mFileName);

    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mRegion.hashCode();
        result = 31 * result + mFileName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Country{" +
                "Name='" + mName + '\'' +
                ", Region='" + mRegion + '\'' +
                ", FileName='" + mFileName + '\'' +
                '}';
    }
}
