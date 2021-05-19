import java.util.ArrayList;
import java.util.List;

public abstract class Osapool {

    private List<Kaart> kaardid;                        //kaardid osapoole käes

    public Osapool() {
        kaardid = new ArrayList<>();
    }

    public int kaartideSumma() {
        boolean juurde11 = false;                                 //true korral on ässaga 11 juurde lisatud
        int summa = 0;
        for (Kaart k : kaardid) {
            if (summa <= 10 && k.getKirjeldus().equals("A")) {    //ässa puhul liidetakse võimaluse korral 11 juurde
                summa += 11;
                juurde11 = true;
            } else {
                summa += k.getVäärtus();
            }
        }
        if (juurde11 && summa > 21)
            summa -= 10;     //olukord, kus enne oli ässa tõttu 11 juurde liidetud, aga summa ületab 21
        return summa;
    }

    public boolean kasÜle() {
        return kaartideSumma() > 21;
    }

    public boolean kas21() {
        return kaartideSumma() == 21;
    }

    public void lisaKaart(Kaart kaart) {
        kaardid.add(kaart);
    }
}
