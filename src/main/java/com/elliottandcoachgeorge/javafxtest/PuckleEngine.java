package com.elliottandcoachgeorge.javafxtest;

import java.util.*;

public class PuckleEngine {

    private static final String GREEN = "\u001b[42;1m";
    private static final String YELLOW = "\u001b[43;1m";
    private static final String GRAY = "\u001b[47;1m";
    private static final String RESET = "\u001b[0m";

    // =========================
    // WORD LIST (from your file)
    // =========================
    private static final String[] WORDS = {
            "aback","abase","abate","abbey","abbot","abhor","abide","abled","abode","abort",
            "about","above","abuse","abyss","acorn","acrid","actor","acute","adage","adapt",
            "adept","admin","admit","adobe","adopt","adore","adorn","adult","affix","afire",
            "afoot","afoul","after","again","agape","agate","agent","agile","aging","aglow",
            "agony","agora","agree","ahead","aider","aisle","alarm","album","alert","algae",
            "alibi","alien","align","alike","alive","allay","alley","allot","allow","alloy",
            "aloft","alone","along","aloof","aloud","alpha","altar","alter","amass","amaze",
            "amber","amble","amend","amiss","amity","among","ample","amply","amuse","angel",
            "anger","angle","angry","angst","anime","ankle","annex","annoy","annul","anode",
            "antic","anvil","aorta","apart","aphid","aping","apnea","apple","apply","apron",
            "aptly","arbor","ardor","arena","argue","arise","armor","aroma","arose","array",
            "arrow","arson","artsy","ascot","ashen","aside","askew","assay","asset","atoll",
            "atone","attic","audio","audit","augur","aunty","avail","avert","avian","avoid",
            "await","awake","award","aware","awash","awful","awoke","axial","axiom","axion",
            "azure","bacon","badge","badly","bagel","baggy","baker","baler","balmy","banal",
            "banjo","barge","baron","basal","basic","basil","basin","basis","baste","batch",
            "bathe","baton","batty","bawdy","bayou","beach","beady","beard","beast","beech",
            "beefy","befit","began","begat","beget","begin","begun","being","belch","belie",
            "belle","belly","below","bench","beret","berry","berth","beset","betel","bevel",
            "bezel","bible","bicep","biddy","bigot","bilge","billy","binge","bingo","biome",
            "birch","birth","bison","bitty","black","blade","blame","bland","blank","blare",
            "blast","blaze","bleak","bleat","bleed","bleep","blend","bless","blimp","blind",
            "blink","bliss","blitz","bloat","block","bloke","blond","blood","bloom","blown",
            "bluff","blunt","blurb","blurt","blush","board","boast","bobby","boney","bongo",
            "bonus","booby","boost","booth","booty","booze","boozy","borax","borne","bosom",
            "bossy","botch","bough","boule","bound","bowel","boxer","brace","braid","brain",
            "brake","brand","brash","brass","brave","bravo","brawl","brawn","bread","break",
            "breed","briar","bribe","brick","bride","brief","brine","bring","brink","briny",
            "brisk","broad","broil","broke","brood","brook","broom","broth","brown","brunt",
            "brush","brute","buddy","budge","buggy","bugle","build","built","bulge","bulky",
            "bully","bunch","bunny","burly","burnt","burst","bused","bushy","butch","butte",
            "buxom","buyer","bylaw","cabal","cabby","cabin","cable","cacao","cache","cacti",
            "caddy","cadet","cagey","cairn","camel","cameo","canal","candy","canny","canoe",
            "canon","caper","caput","carat","cargo","carol","carry","carve","caste","catch",
            "cater","catty","caulk","cause","cavil","cease","cedar","cello","chafe","chaff",
            "chain","chair","chalk","champ","chant","chaos","chard","charm","chart","chase",
            "chasm","cheap","cheat","check","cheek","cheer","chess","chest","chick","chide",
            "chief","child","chili","chill","chime","china","chirp","chock","choir","choke",
            "chord","chore","chose","chuck","chump","chunk","churn","chute","cider","cigar",
            "cinch","circa","civic","civil","clean","clear","clerk","click","cliff","climb",
            "clock","close","cloud","crown","crush","crust","cubic","cycle","daily","dance",
            "death","delay","delta","demon","devil","diary","dirty","doubt","drama","dream",
            "drink","drive","eager","early","earth","eagle","edict","eight","elect","empty",
            "enemy","enjoy","entry","equal","error","event","every","exact","exist","extra",
            "faith","fancy","fault","feast","fence","field","fight","final","flame","flash",
            "float","floor","force","frame","fresh","front","fruit","giant","ghost","globe",
            "grace","grade","grain","grand","grant","grass","grave","great","green","group",
            "guard","guess","guide","habit","happy","heart","heavy","honor","human","image",
            "index","input","judge","knife","learn","light","logic","lucky","magic","march",
            "match","model","money","month","music","nasty","nerve","never","night","noble",
            "noise","north","novel","ocean","offer","often","olive","opera","order","other",
            "paint","panel","party","peace","phone","photo","piece","pilot","place","plane",
            "plant","plate","point","power","pride","print","prize","proof","queen","quick",
            "quiet","radio","raise","reach","react","ready","reply","right","river","robin",
            "rough","round","route","royal","rural","sauce","scale","scene","scope","score",
            "sense","shape","share","sharp","sheep","sheet","shell","shift","shine","shirt",
            "shock","shoot","shore","short","sight","skill","sleep","smile","sound","space",
            "spare","speak","spend","spine","sport","stage","stand","start","state","stick",
            "stone","store","story","stuck","study","style","sugar","super","sweet","table",
            "taste","teach","theme","thing","think","third","thorn","throw","tight","title",
            "today","tooth","touch","tower","track","trade","train","trend","trial","tribe",
            "truck","trust","truth","uncle","under","union","unity","urban","usage","usual",
            "value","video","virus","visit","vital","vocal","voice","watch","water","wheel",
            "where","which","while","white","whole","woman","world","worry","wound","write",
            "wrong","youth","zebra","zesty","zonal"
    };

    private String target;

    public PuckleEngine() {
        target = WORDS[(int)(Math.random() * WORDS.length)];
    }

    public void play() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a 5-letter word:");

        for (int attempt = 1; attempt <= 6; attempt++) {

            String input = scanner.nextLine().toLowerCase().trim();

            if (input.length() != 5) {
                System.out.println("Must be exactly 5 letters.");
                attempt--;
                continue;
            }

            char[] t = target.toCharArray();
            char[] g = input.toCharArray();

            String[] out = new String[5];
            boolean[] used = new boolean[5];

            // greens
            for (int i = 0; i < 5; i++) {
                if (g[i] == t[i]) {
                    out[i] = GREEN + g[i];
                    used[i] = true;
                }
            }

            // yellows + grays
            for (int i = 0; i < 5; i++) {
                if (out[i] != null) continue;

                boolean found = false;

                for (int j = 0; j < 5; j++) {
                    if (!used[j] && g[i] == t[j]) {
                        used[j] = true;
                        found = true;
                        break;
                    }
                }

                out[i] = (found ? YELLOW : GRAY) + g[i];
            }

            for (String s : out) System.out.print(s);
            System.out.println(RESET);

            if (input.equals(target)) {
                System.out.println("You won in " + attempt + " guesses!");
                return;
            }
        }

        System.out.println("You lost! Word was: " + target);
    }

    public static void main(String[] args) {
        new PuckleEngine().play();
    }
}