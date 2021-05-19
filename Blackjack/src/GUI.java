import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GUI implements ActionListener {
    private JLabel Panus = new JLabel("Sisestatud panus: ");
    private JLabel Raha;
    private double raha = 200;
    private double panus;
    private int mängijaX = 60;
    private int diileriX = 60;
    private List<Kaart> pakk = new ArrayList<>();
    private Mängija mängija = new Mängija();
    private Diiler diiler = new Diiler();
    private Kaart[] algnePakk = new Kaart[52];
    private boolean hititud = false;
    private JFrame frame = new JFrame();
    private JButton hit = new JButton("Hit");
    private JButton stand = new JButton("Stand");
    private JButton double_down = new JButton("Double down");
    private JPanel ylemine = new JPanel();
    private JLabel YLabel = new JLabel("Diiler:");
    private JPanel keskmine = new JPanel();
    private JLabel KLabel = new JLabel("Sina:");
    private JPanel alumine = new JPanel();
    private JPanel eelviimane = new JPanel();
    private JLabel sõnum;
    private JLabel sisestaPanus;
    private JLabel peidetud_kaart_pilt;
    private Kaart peidetud_kaart;
    private Action Hit;
    private Action Stand;
    private Action DoubleDown;
    private Action tekstiväljaAction;
    private double parim = 200;

    public GUI() {
        YLabel.setForeground(Color.WHITE);
        KLabel.setForeground(Color.WHITE);
        Raha = new JLabel("Raha: " + raha);
        sõnum = new JLabel("Tere tulemast mängu Blackjack!");
        JLabel sisestaPanus = new JLabel("Roundi alustamiseks sisesta oma panus:");
        Color darkGreen = new Color(0, 100, 0);
        JTextField tekstivali = new JFormattedTextField();

        //diileri panel
        ylemine.setBorder(BorderFactory.createMatteBorder(100, 10, 300, 1000, darkGreen));
        ylemine.add(YLabel);

        //mängija panel
        keskmine.setBorder(BorderFactory.createMatteBorder(100, 10, 300, 1000, darkGreen));
        keskmine.add(KLabel);

        //nuppude/sõnumi panel
        eelviimane.setBorder(BorderFactory.createMatteBorder(50, 50, 50, 50, darkGreen));
        eelviimane.setLayout(new GridLayout(2, 3, 10, 10));
        eelviimane.add(new JLabel());
        eelviimane.add(sõnum);
        eelviimane.add(new JLabel());
        eelviimane.add(hit);
        eelviimane.add(stand);
        eelviimane.add(double_down);

        //raha/panuse panel
        alumine.setBorder(BorderFactory.createMatteBorder(50, 50, 50, 50, Color.lightGray));
        alumine.setLayout(new GridLayout(2, 3, 10, 10));
        alumine.add(sisestaPanus);
        alumine.add(tekstivali);
        alumine.add(Panus);
        alumine.add(new JLabel());
        alumine.add(new JLabel());
        alumine.add(Raha);

        // set up the frame and display it
        frame.setLayout(new GridLayout(4, 1));
        frame.add(ylemine);
        frame.add(keskmine);
        frame.add(eelviimane);
        frame.add(alumine);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Blackjack");
        frame.pack();
        frame.setVisible(true);

        //kaardipaki genereerimine
        String[] mastinimed = {"ruutu", "risti", "ärtu", "poti"};
        String[] väärtused = {"A", "2", "3", "4", "5", "6", "7",
                "8", "9", "10", "J", "Q", "K"};
        String[] väärtusenimed = {"äss", "kaks", "kolm", "neli", "viis", "kuus", "seitse",
                "kaheksa", "üheksa", "kümme", "poiss", "emand", "kuningas"};

        int lugeja = 0;
        for (int i = 0; i < mastinimed.length; i++) {
            for (int j = 0; j < väärtused.length; j++) {
                String kirjeldus = väärtused[j];
                if (j < 9)
                    algnePakk[lugeja] = new Kaart(j + 1, kirjeldus, mastinimed[i] + väärtusenimed[j]);
                else {
                    algnePakk[lugeja] = new Kaart(10, kirjeldus, mastinimed[i] + väärtusenimed[j]);
                }
                lugeja++;
            }
        }

        this.Hit = new AbstractAction() {   //hit nupp
            @Override
            public void actionPerformed(ActionEvent e) {
                hititud = true;
                Kaart hitkaart = pakk.get((int) (Math.random() * pakk.size()));
                mängija.lisaKaart(hitkaart);
                kuvaKaart(keskmine, hitkaart, mängijaX);
                pakk.remove(hitkaart);
                sõnum.setText("Sinu kaartide summa on " + mängija.kaartideSumma() + ". Sinu valikud:");
                mängijaX += 75;     //nihutame x-koordinaati lisatud kaardi laiuse võrra edasi
                checkÜle();     //vaatame kas bust
                if (mängija.kas21()) endingCheck(); //läheb kontrollimiseks
            }
        };

        this.Stand = new AbstractAction() {     //stand nupp
            @Override
            public void actionPerformed(ActionEvent e) {
                endingCheck();          //läheb kontrollimiseks
            }
        };

        this.DoubleDown = new AbstractAction() {    //double down nupp
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!hititud) {     //saab ainult esimese tegevusena teha
                    if (raha >= panus) {
                        raha -= panus;
                        Raha.setText("Raha: " + raha);
                        panus *= 2;
                        Panus.setText("Sisestatud panus: " + panus);
                        Kaart hitkaart = pakk.get((int) (Math.random() * pakk.size()));
                        mängija.lisaKaart(hitkaart);
                        kuvaKaart(keskmine, hitkaart, mängijaX);
                        pakk.remove(hitkaart);
                        mängijaX += 75;
                        if (!checkÜle()) endingCheck();
                    } else sõnum.setText("Sul pole piisavalt raha.");
                } else sõnum.setText("Enam ei saa panust suurendada!");
            }
        };

        this.tekstiväljaAction = new AbstractAction() {       //tekstivälja sisestamine
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    hit.addActionListener(Hit);
                    stand.addActionListener(Stand);
                    double_down.addActionListener(DoubleDown);
                    panus = Double.parseDouble(tekstivali.getText());
                    if (panus > raha)
                        throw new ValeSisendException("Sul pole nii palju raha.");
                    if (panus <= 0)
                        throw new ValeSisendException("Panus peab olema positiivse väärtusega.");
                    raha -= panus;
                    tekstivali.setText("");
                    sõnum.setText("Alustame roundiga. Sinu valikud:");
                    Panus.setText("Sisestatud panus: " + panus);
                    Raha.setText("Raha: " + raha);
                    sisestaPanus.setVisible(false);

                    //loome mängitava kaardipaki
                    pakk = new ArrayList<>();
                    Arrays.stream(algnePakk).spliterator().forEachRemaining(pakk::add);
                    Collections.shuffle(pakk);

                    for (int i = 0; i < 2; i++) {   //jagame diilerile ja mängijale mõlemale kaks kaarti
                        Kaart mängijaKaart = pakk.get((int) (Math.random() * pakk.size()));
                        mängija.lisaKaart(mängijaKaart);
                        kuvaKaart(keskmine, mängijaKaart, mängijaX);
                        mängijaX += 75; //nihutame x-koordinaati lisatud kaardi laiuse võrra edasi
                        pakk.remove(mängijaKaart);

                        Kaart diileriKaart = pakk.get((int) (Math.random() * pakk.size()));
                        if (i == 1) { //teine diileri kaart on peidetud
                            String kaarditee_a = diileriKaart.getKaarditee();
                            diileriKaart.setKaarditee("Kaardid/tagune.jpg");
                            kuvaKaart(ylemine, diileriKaart, diileriX);
                            diileriKaart.setKaarditee(kaarditee_a);
                            diiler.lisaKaart(diileriKaart);
                            peidetud_kaart = diileriKaart;
                        } else {
                            diiler.lisaKaart(diileriKaart);
                            kuvaKaart(ylemine, diileriKaart, diileriX);
                            diileriX += 75;
                            pakk.remove(diileriKaart);
                        }
                    }
                    check21(); //kontrollime kas on kohe 21 käes
                } catch (ValeSisendException vale) {
                    System.out.println(vale.getMessage());
                    sõnum.setText(vale.getMessage());
                    tekstivali.setText("");
                }

            }
        };
        tekstivali.addActionListener(tekstiväljaAction);
    }

    public void check21() {     //kui kahe esimese kaardiga 21 kontroll
        if (mängija.kas21()) {
            if (diiler.kas21()) {
                eelviimaseReset("Push! Saad " + panus + "€ tagasi.");
                raha += panus;
            } else {
                eelviimaseReset("Blackjack! Võidad " + panus * 3 + "€.");
                raha += 3 * panus;
            }
        }
    }

    public boolean checkÜle() {     //kontrollime kas kaartide summa üle 21-e
        if (mängija.kasÜle()) {
            eelviimaseReset("Bust! Kaotad " + panus + "€.");
            return true;
        }
        return false;
    }

    public void endingCheck() {     //diileri kaardi revealimne/juurdevõtmine ja võitja selgitamine + raha faili kirjutamine
        try {   //näitame peidetud kaarti
            peidetud_kaart_pilt.setIcon(new ImageIcon(ImageIO.read(new File(peidetud_kaart.getKaarditee()))));
        } catch (IOException e) {
            System.out.println("Ei leidnud faili.");
        }
        while (diiler.kasAlla17()) { //diiler saab kaarte juurde
            Kaart diileriKaart = pakk.get((int) (Math.random() * pakk.size()));
            diiler.lisaKaart(diileriKaart);
            kuvaKaart(ylemine, diileriKaart, diileriX);
            pakk.remove(diileriKaart);
            diileriX += 75;
        }

        //leiame võitja
        if (diiler.kasÜle() || diiler.kaartideSumma() < mängija.kaartideSumma()) {
            eelviimaseReset("Sinu võit! Võidad " + panus * 2 + "€.");
            raha += panus * 2;
        } else if (mängija.kasÜle() || diiler.kaartideSumma() > mängija.kaartideSumma()) {
            eelviimaseReset("Diileri võit! Kaotad " + panus + "€.");
        } else if (diiler.kaartideSumma() == mängija.kaartideSumma()) {
            eelviimaseReset("Push! Saad " + panus + "€ tagasi.");
            raha += panus;
        }

        if (raha > parim) {     //kirjutame parima käesoleva rahasumma faili
            try {
                System.out.println("tuleb bf");
                BufferedWriter bw = new BufferedWriter(new FileWriter("parim.txt"));
                System.out.println("bf olemas");
                bw.write("Parim tulemus: " + raha);
                System.out.println("bw kirjas");
                bw.close();
                parim = raha;
            } catch (IOException e) {
                System.out.println("juhtub");
            }
        }
    }

    public void reset() {
        Component[] acomponents = alumine.getComponents(); //tühjendame alumise paneli
        for (Component component : acomponents) {
            alumine.remove(component);
        }
        alumine.revalidate();
        alumine.repaint();

        Component[] kcomponents = keskmine.getComponents(); //tühjendame mängija paneli
        for (Component component : kcomponents) {
            keskmine.remove(component);
        }
        keskmine.revalidate();
        keskmine.repaint();

        Component[] ycomponents = ylemine.getComponents();  //tühjendame diileri paneli
        for (Component component : ycomponents) {
            ylemine.remove(component);
        }
        ylemine.revalidate();
        ylemine.repaint();

        this.mängija = new Mängija();
        this.diiler = new Diiler();

        //resetime x-koordinaadi millest alates hakatakse kaarte paigutama
        mängijaX = 60;
        diileriX = 60;

        ylemine.add(YLabel); //lisame uuesti "Diiler" ja "Sina" märksõnad
        keskmine.add(KLabel);
    }

    public void eelviimaseReset(String sõnum) {//teeme eelviimase paneli tühjaks ja lisame uued vajalikud komponendid
        Component[] eelcomponents = eelviimane.getComponents();
        for (Component component : eelcomponents) {
            eelviimane.remove(component);   //tühjendame
        }
        eelviimane.revalidate();
        eelviimane.repaint();

        //lisame uued komponendid
        eelviimane.add(new JLabel()); //filler väli
        eelviimane.add(raha != 0 ? new JLabel(sõnum) : new JLabel("Mäng läbi! Kaotasid kogu oma raha!")); //kas raha otsas v mitte
        eelviimane.add(new JLabel());
        eelviimane.add(new JLabel());
        JButton uuesti = new JButton();
        if (raha != 0) {
            uuesti.setText("Alusta uuesti");
            uuesti.addActionListener(e -> init());
        } else {    //kui raha otsas on mäng läbi
            uuesti.setText("Välju");
            uuesti.addActionListener(e -> System.exit(1));
        }
        eelviimane.add(uuesti);
        eelviimane.add(new JLabel());
    }

    public void kuvaKaart(JPanel panel, Kaart kaart, int xCord) { //kuvame lisatud kaardi pildi mänguväljale
        try {
            BufferedImage image = ImageIO.read(new File(kaart.getKaarditee()));
            if (kaart.getKaarditee().equals("Kaardid/tagune.jpg")) { //peidetud diileri kaart
                peidetud_kaart_pilt = new JLabel(new ImageIcon(image));
                panel.setLayout(null);
                peidetud_kaart_pilt.setLocation(xCord, 70);
                panel.add(peidetud_kaart_pilt);
                peidetud_kaart_pilt.setSize(100, 100);
                peidetud_kaart_pilt.setVisible(true);
                diileriX += 75;
            } else {
                JLabel picLabel = new JLabel(new ImageIcon(image));
                panel.setLayout(null);
                picLabel.setLocation(xCord, 70);
                panel.add(picLabel);
                picLabel.setSize(100, 100);
                picLabel.setVisible(true);
            }
        } catch (IOException ex) {
            System.out.println("Vigane kaardi path!");
        }
    }

    public void removeListeners() { //eemaldame listenerid, et uuesti initsialiseerimisel ei tuleks neid topelt
        JButton[] buttons = {hit, stand, double_down};
        for (JButton currentButton : buttons) {
            for (ActionListener al : currentButton.getActionListeners()) {
                currentButton.removeActionListener(al);
            }
        }
    }

    public void init() { //initsialiseerime uue mängu
        removeListeners();
        reset();
        this.hititud = false;
        Component[] ecomponents = eelviimane.getComponents();
        for (Component component : ecomponents) {
            eelviimane.remove(component);
        }
        eelviimane.revalidate();
        eelviimane.repaint();

        sisestaPanus = new JLabel("Roundi alustamiseks sisesta oma panus:");
        Raha = new JLabel("Raha: " + raha);
        JTextField tekstivali = new JFormattedTextField();
        sõnum = new JLabel("Teretulemast mängu Blackjack!");
        Color darkGreen = new Color(0, 100, 0);

        ylemine.setBorder(BorderFactory.createMatteBorder(100, 10, 300, 1000, darkGreen));
        ylemine.add(YLabel);

        keskmine.setBorder(BorderFactory.createMatteBorder(100, 10, 300, 1000, darkGreen));
        keskmine.add(KLabel);

        eelviimane.setBorder(BorderFactory.createMatteBorder(50, 50, 50, 50, darkGreen));
        eelviimane.setLayout(new GridLayout(2, 3, 10, 10));
        eelviimane.add(new JLabel());
        eelviimane.add(sõnum);
        eelviimane.add(new JLabel());
        eelviimane.add(hit);
        eelviimane.add(stand);
        eelviimane.add(double_down);

        alumine.setBorder(BorderFactory.createMatteBorder(50, 50, 50, 50, Color.lightGray));
        alumine.setLayout(new GridLayout(2, 3, 10, 10));
        alumine.add(sisestaPanus);
        alumine.add(tekstivali);
        alumine.add(Panus);
        alumine.add(new JLabel());
        alumine.add(new JLabel());
        alumine.add(Raha);

        //genereerime kaardipaki
        String[] mastinimed = {"ruutu", "risti", "ärtu", "poti"};
        String[] väärtused = {"A", "2", "3", "4", "5", "6", "7",
                "8", "9", "10", "J", "Q", "K"};
        String[] väärtusenimed = {"äss", "kaks", "kolm", "neli", "viis", "kuus", "seitse",
                "kaheksa", "üheksa", "kümme", "poiss", "emand", "kuningas"};

        Kaart[] algnePakk = new Kaart[52];
        int lugeja = 0;
        for (int i = 0; i < mastinimed.length; i++) {
            for (int j = 0; j < väärtused.length; j++) {
                String kirjeldus = väärtused[j];
                if (j < 9)
                    algnePakk[lugeja] = new Kaart(j + 1, kirjeldus, mastinimed[i] + väärtusenimed[j]);
                else {
                    algnePakk[lugeja] = new Kaart(10, kirjeldus, mastinimed[i] + väärtusenimed[j]);
                }
                lugeja++;
            }
        }

        Action tekstiväljaAction = new AbstractAction() { //tekstivälja sisestamine
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    removeListeners();
                    hit.addActionListener(Hit);
                    stand.addActionListener(Stand);
                    double_down.addActionListener(DoubleDown);
                    panus = Double.parseDouble(tekstivali.getText());
                    if (panus > raha)
                        throw new ValeSisendException("Sul pole nii palju raha.");
                    if (panus <= 0)
                        throw new ValeSisendException("Panus peab olema positiivse väärtusega.");
                    raha -= panus;
                    tekstivali.setText("");
                    sõnum.setText("Alustame roundiga. Sinu valikud:");
                    Panus.setText("Sisestatud panus: " + panus);
                    Raha.setText("Raha: " + raha);
                    sisestaPanus.setVisible(false);

                    pakk = new ArrayList<>();
                    Arrays.stream(algnePakk).spliterator().forEachRemaining(pakk::add);
                    Collections.shuffle(pakk);

                    for (int i = 0; i < 2; i++) {
                        Kaart mängijaKaart = pakk.get((int) (Math.random() * pakk.size()));
                        mängija.lisaKaart(mängijaKaart);
                        kuvaKaart(keskmine, mängijaKaart, mängijaX);
                        mängijaX += 75;
                        pakk.remove(mängijaKaart);

                        Kaart diileriKaart = pakk.get((int) (Math.random() * pakk.size()));
                        if (i == 1) {
                            String kaarditee_a = diileriKaart.getKaarditee();
                            diileriKaart.setKaarditee("Kaardid/tagune.jpg");
                            kuvaKaart(ylemine, diileriKaart, diileriX);
                            diileriKaart.setKaarditee(kaarditee_a);
                            diiler.lisaKaart(diileriKaart);
                            peidetud_kaart = diileriKaart;
                        } else {
                            diiler.lisaKaart(diileriKaart);
                            kuvaKaart(ylemine, diileriKaart, diileriX);
                            diileriX += 75;
                            pakk.remove(diileriKaart);
                        }
                    }
                    check21();
                } catch (ValeSisendException vale) {
                    System.out.println(vale.getMessage());
                    sõnum.setText(vale.getMessage());
                    tekstivali.setText("");
                }

            }
        };
        tekstivali.addActionListener(tekstiväljaAction);
    }

    // process the button clicks
    public void actionPerformed(ActionEvent e) {
    }

    // create one Frame
    public static void main(String[] args) {
        new GUI();
    }
}
