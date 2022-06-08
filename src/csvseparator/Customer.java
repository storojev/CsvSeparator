/**
 *   class Customer.java (Version 1.0.0.0)
 *   
 *   Customer class is used to group customer's information.
 */

package csvseparator;

///////////////////////////////////////////////////////////////////////////////
public class Customer {

    private String id = "";
    private String first = "";
    private String last = "";
    private String provider = "";
    private int version = 0;

    //---------------------------------------------------------------------------
    public String getId() {
        return id;
    }

    public void setId(String newId) {
        this.id = newId;
    }

    //---------------------------------------------------------------------------
    public String getFirst() {
        return first;
    }

    public void setFirst(String newFirst) {
        this.first = newFirst;
    }

    //---------------------------------------------------------------------------
    public String getLast() {
        return last;
    }

    public void setLast(String newLast) {
        this.last = newLast;
    }

    //---------------------------------------------------------------------------
    public String getProvider() {
        return provider;
    }

    public void setProvider(String newProvider) {
        this.provider = newProvider;
    }

    //---------------------------------------------------------------------------
    public int getVersion() {
        return version;
    }

    public void setVersion(int newVersion) {
        this.version = newVersion;
    }

    //---------------------------------------------------------------------------

}