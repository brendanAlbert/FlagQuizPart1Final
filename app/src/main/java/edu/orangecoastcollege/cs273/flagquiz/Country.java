package edu.orangecoastcollege.cs273.flagquiz;


/**
 * This Country class is used to model Country objects.
 *
 * A 'Country' has a name, region and filename.
 *
 * We use 10 Country objects for the user to guess their respective
 * flags from, which is how the app is used/played.
 *
 * There are accessor and mutator methods.
 * There is a constructor which creates the flag's fileName
 * from the country's region and name and appends .png.
 *
 * The last three methods: equals, getHashCode and toString
 * are overriden from Java's Object class.  These allow two
 * countries to be compared or displayed.
 */
public class Country {

    private String mName;
    private String mRegion;
    private String mFileName;

    /**
     *
     * Country is a parameterized constructor which accepts two arguments,
     * name and region.  The file name of the picture to be looked up and
     * displayed is constructed from the name and region appended to .png.
     *
     * @param name of the country.
     * @param region which area of the world the country resides.
     */
    public Country(String name, String region) {
        mName = name;
        mRegion = region;
        name = name.replaceAll(" ", "_");
        region = region.replaceAll(" ", "_");
        mFileName = region + "/" + region + "-" + name + ".png";
    }

    /**
     *
     * @return the country's name.
     */
    public String getName() { return mName; }

    /**
     *
     * @param name is the name of the country.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     *
     * @return get country's region of the world where it resides,
     * i.e. North America or Africa.
     */
    public String getRegion() {
        return mRegion;
    }

    /**
     * 
     * @param region the name of the region to be set.
     */
    public void setRegion(String region) {
        mRegion = region;
    }

    /**
     *
     * @return the fileName of the Country's flag as a .png
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     *
     * @param fileName is constructed from a Country's name and region
     */
    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    /**
     * This method is inherited by all objects in Java.
     * We must write our own implementation which makes sense for a Country object.
     * If the two objects' names and regions are the same then the Countries are the same.
     * @param o is the object "this" object is being compared against
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (!mName.equals(country.mName)) return false;
        if (!mRegion.equals(country.mRegion)) return false;
        return mFileName.equals(country.mFileName);

    }

    /**
     * This is another method inherited by all objects in Java.
     * hashCode is used to ensure that if two objects are the same according to the equals method,
     * then their hashCodes should match.  i.e. if object1 == object2 then their hashCodes
     * are the same.
     * @return
     */
    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mRegion.hashCode();
        result = 31 * result + mFileName.hashCode();
        return result;
    }

    /**
     * Yet another method inherited by all objects in Java.
     * The default object implementation of this method provides scant information.
     * By overriding toString, when we print an object, we can customize what information
     * will be displayed.
     * @return
     */
    @Override
    public String toString() {
        return "Country{" +
                "Name='" + mName + '\'' +
                ", Region='" + mRegion + '\'' +
                ", FileName='" + mFileName + '\'' +
                '}';
    }
}
