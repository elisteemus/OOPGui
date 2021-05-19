public class Kaart {

    private int väärtus;                //kaardi punktide arv
    private final String kirjeldus;     //kaardi tüüp
    private String kaarditee;           //kaardi pildifaili nimi


    public Kaart(int väärtus, String kirjeldus, String kaarditee) {
        this.väärtus = väärtus;
        this.kirjeldus = kirjeldus;
        this.kaarditee = "Kaardid/"+kaarditee+".jpg";
    }

    public String getKirjeldus() {
        return kirjeldus;
    }

    public int getVäärtus() {
        return väärtus;
    }

    public String getKaarditee() {
        return kaarditee;
    }

    public void setKaarditee(String kaarditee){
        this.kaarditee = kaarditee;
    }
}
