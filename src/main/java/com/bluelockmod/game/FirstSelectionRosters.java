package com.bluelockmod.game;

import com.bluelockmod.ai.AIRole;
import com.bluelockmod.player.PlayerStats.Stat;

import java.util.*;

/** Canon-inspired First Selection rosters. Named players and weapons follow the anime/manga; numeric ratings are mod gameplay values. */
public final class FirstSelectionRosters {
    private FirstSelectionRosters() {}
    private static EnumMap<Stat,Integer> s(int speed,int accel,int stamina,int strength,int control,int dribble,int pass,int shoot,int finish,int vision,int spatial,int position,int decision,int tackle,int intercept,int marking) {
        EnumMap<Stat,Integer> m=new EnumMap<>(Stat.class);
        m.put(Stat.TOP_SPEED,speed); m.put(Stat.ACCELERATION,accel); m.put(Stat.STAMINA,stamina); m.put(Stat.STRENGTH,strength);
        m.put(Stat.BALL_CONTROL,control); m.put(Stat.DRIBBLING,dribble); m.put(Stat.PASSING,pass); m.put(Stat.SHOOTING,shoot);
        m.put(Stat.FINISHING,finish); m.put(Stat.VISION,vision); m.put(Stat.SPATIAL_AWARENESS,spatial); m.put(Stat.POSITIONING,position);
        m.put(Stat.DECISION_MAKING,decision); m.put(Stat.TACKLING,tackle); m.put(Stat.INTERCEPTION,intercept); m.put(Stat.MARKING,marking);
        return m;
    }
    private static CanonPlayer p(String n,int no,AIRole r,String w,EnumMap<Stat,Integer> s){return new CanonPlayer(n,no,r,w,s);}

    public static List<CanonPlayer> teamZ() { return List.of(
        p("Yoichi Isagi",11,AIRole.STRIKER,"Spatial awareness / direct attacking",s(72,68,74,58,72,67,70,72,70,88,91,76,86,38,42,44)),
        p("Meguru Bachira",8,AIRole.WINGER,"Creative dribbling",s(82,84,78,60,91,95,82,72,70,78,79,72,80,34,38,40)),
        p("Rensuke Kunigami",9,AIRole.STRIKER,"Powerful left-footed shooting",s(76,70,79,86,70,65,58,91,86,55,52,65,64,42,38,45)),
        p("Hyoma Chigiri",4,AIRole.WINGER,"Explosive speed",s(96,94,72,56,68,76,55,68,63,62,67,79,71,36,40,43)),
        p("Gin Gagamaru",6,AIRole.GOALKEEPER,"Acrobatic physical ability",s(84,86,82,79,78,69,48,55,50,61,70,82,76,70,78,72)),
        p("Jingo Raichi",10,AIRole.MIDFIELDER,"Man-marking and stamina",s(70,73,94,82,62,57,54,62,57,48,50,68,66,91,79,92)),
        p("Wataru Kuon",2,AIRole.DEFENDER,"Planning and aerial play",s(65,63,73,68,65,50,74,60,58,80,72,85,84,62,73,77)),
        p("Gurimu Igarashi",13,AIRole.DEFENDER,"Survival / foul-drawing",s(61,66,70,52,55,48,45,40,35,43,45,61,60,58,48,61)),
        p("Asahi Naruhaya",5,AIRole.WINGER,"Off-ball movement",s(75,78,76,54,64,70,48,55,52,57,70,80,68,30,34,37)),
        p("Yudai Imamura",7,AIRole.MIDFIELDER,"Technical attacking play",s(78,80,76,58,76,72,72,66,63,68,67,69,72,32,35,39)),
        p("Okuhito Iemon",1,AIRole.GOALKEEPER,"Goalkeeping and distribution",s(64,62,75,68,62,40,60,42,35,63,58,82,70,67,72,75))
    ); }
    public static List<CanonPlayer> teamX() { return List.of(
        p("Shoei Barou",10,AIRole.STRIKER,"Powerful shooting and predator positioning",s(88,84,82,91,76,78,48,95,94,72,76,92,88,40,38,44)),
        p("Haato Meiji",6,AIRole.MIDFIELDER,"Link play",s(67,68,70,58,61,56,66,55,51,58,54,62,65,35,38,40)),
        p("Daiya Morinaga",4,AIRole.DEFENDER,"Tactical organization",s(61,60,69,67,58,43,60,45,42,68,62,74,77,70,71,78)),
        p("Yuza Dokomo",1,AIRole.GOALKEEPER,"Goalkeeping",s(62,60,70,65,60,38,52,38,32,55,50,76,66,65,69,71)),
        p("Tsukoteru Eiyu",2,AIRole.DEFENDER,"Defensive positioning",s(64,65,68,65,58,45,49,43,39,45,46,70,63,72,70,74)),
        p("Yawara Banku",3,AIRole.DEFENDER,"Physical defending",s(59,58,73,79,55,38,42,42,35,38,40,66,57,79,72,76)),
        p("Chihiro Ezaki",5,AIRole.DEFENDER,"Marking",s(67,66,70,61,58,46,48,44,38,42,45,71,60,76,75,81)),
        p("Kosei Otsuka",7,AIRole.MIDFIELDER,"Support running",s(72,74,73,55,62,57,61,49,45,50,54,61,59,35,39,43)),
        p("Ruto Kora",8,AIRole.WINGER,"Wing running",s(79,82,72,55,62,66,51,53,48,48,53,63,58,30,34,38)),
        p("Rian Sanga",11,AIRole.STRIKER,"Support / finishing",s(72,70,70,60,60,51,52,65,61,48,50,70,62,29,32,35)),
        p("Burai Daido",9,AIRole.WINGER,"Direct attack",s(76,77,71,60,62,61,46,58,54,44,49,61,57,31,33,36))
    ); }
    public static List<CanonPlayer> teamY() { return List.of(
        p("Ikki Niko",7,AIRole.MIDFIELDER,"One-time kill counter / spatial reading",s(67,65,73,54,69,54,78,53,49,89,90,86,91,67,84,72)),
        p("Hibiki Okawa",9,AIRole.STRIKER,"Finishing",s(70,68,70,63,61,52,47,68,66,44,48,72,63,28,31,34)),
        p("Juraki Ito",1,AIRole.GOALKEEPER,"Goalkeeping",s(61,59,69,64,59,37,51,37,31,52,49,75,64,63,68,70)),
        p("Ashime Suzuki",2,AIRole.DEFENDER,"Marking",s(61,60,70,66,55,39,43,40,35,40,42,67,59,74,72,79)),
        p("Tobio Madoka",3,AIRole.DEFENDER,"Coverage",s(62,61,71,65,56,40,45,41,36,41,43,68,60,73,73,77)),
        p("Shinichi Konan",4,AIRole.DEFENDER,"Defending",s(60,59,70,67,54,38,42,39,34,39,41,66,57,76,74,80)),
        p("Iori Sato",5,AIRole.DEFENDER,"Positioning",s(64,63,71,64,57,42,47,41,35,46,48,73,65,71,72,78)),
        p("Soshi Kagura",6,AIRole.MIDFIELDER,"Support",s(66,67,70,56,61,51,63,46,41,54,52,63,61,35,39,43)),
        p("Fuma Rokkaku",8,AIRole.MIDFIELDER,"Passing",s(68,67,72,57,64,53,67,47,42,64,59,62,67,33,37,41)),
        p("Mareto Takeyama",10,AIRole.STRIKER,"Finishing",s(71,69,71,61,60,51,45,67,64,44,47,71,62,28,31,34)),
        p("Hyuga Koshiba",11,AIRole.WINGER,"Running",s(73,76,70,56,60,55,49,51,45,47,50,63,57,31,34,38))
    ); }
    public static List<CanonPlayer> teamW() { return List.of(
        p("Junichi Wanima",10,AIRole.STRIKER,"Combination play",s(73,75,75,62,65,62,70,62,58,60,58,70,71,35,38,41)),
        p("Keisuke Wanima",11,AIRole.STRIKER,"Combination play",s(72,74,76,61,66,63,72,60,57,61,59,69,72,34,37,40)),
        p("Kei Shishiya",7,AIRole.MIDFIELDER,"Link play",s(67,68,70,57,60,52,62,45,41,53,50,62,60,35,39,42)),
        p("Hiromu Munakata",6,AIRole.MIDFIELDER,"Support",s(66,67,69,56,58,50,60,44,39,50,49,61,58,36,40,43)),
        p("Yusei Amazora",8,AIRole.WINGER,"Running",s(74,77,70,58,59,55,48,50,44,46,50,62,56,32,35,39)),
        p("Takuma Isezaki",9,AIRole.STRIKER,"Finishing",s(69,68,71,60,60,50,46,65,61,43,45,70,61,28,31,34)),
        p("Raito Fuwa",5,AIRole.DEFENDER,"Marking",s(61,60,71,66,55,39,43,40,35,40,42,67,59,74,72,79)),
        p("Noboru Jigen",4,AIRole.DEFENDER,"Defending",s(60,59,72,68,54,38,42,39,34,38,40,66,57,76,73,80)),
        p("Koki Mera",3,AIRole.DEFENDER,"Coverage",s(60,59,70,66,54,39,43,40,35,39,41,65,58,73,71,78)),
        p("Kai Tokita",2,AIRole.DEFENDER,"Interception",s(62,61,72,67,55,41,46,39,34,42,44,67,61,75,78,76)),
        p("Yujin Koshinaka",1,AIRole.GOALKEEPER,"Goalkeeping",s(60,58,68,64,57,36,49,36,30,50,47,74,63,62,67,69))
    ); }
    public static List<CanonPlayer> teamV() { return List.of(
        p("Seishiro Nagi",11,AIRole.STRIKER,"Extraordinary trapping and first touch",s(79,74,66,78,98,89,77,92,91,82,80,88,89,28,32,36)),
        p("Reo Mikage",10,AIRole.MIDFIELDER,"Versatility / wide vision",s(77,76,78,70,82,77,88,72,68,86,80,84,87,48,53,57)),
        p("Zantetsu Tsurugi",9,AIRole.WINGER,"Acceleration and speed",s(95,97,73,60,64,71,47,62,57,45,50,60,55,31,34,38)),
        p("Sota Nemoto",7,AIRole.MIDFIELDER,"Support",s(70,70,72,59,62,53,63,48,43,52,50,64,61,34,38,42)),
        p("Shuhei Ebina",8,AIRole.WINGER,"Running",s(75,78,71,57,61,58,49,52,46,47,51,63,57,30,34,38)),
        p("Masumi Atatame",6,AIRole.MIDFIELDER,"Passing",s(67,67,71,56,63,52,68,47,42,64,58,62,66,33,37,41)),
        p("Kisaburo Hijikata",1,AIRole.GOALKEEPER,"Goalkeeping",s(62,61,70,68,55,39,44,40,35,42,45,70,62,77,75,81)),
        p("Kanji Torikai",4,AIRole.DEFENDER,"Physical defending",s(61,60,73,76,54,37,42,40,34,38,41,67,58,80,75,79)),
        p("Hirakazu Midorikawa",3,AIRole.DEFENDER,"Positioning",s(64,63,71,65,56,42,48,41,35,47,49,74,67,72,74,80)),
        p("Retsu Nerima",2,AIRole.DEFENDER,"Coverage",s(63,62,70,64,55,40,45,39,34,41,43,69,60,74,73,78)),
        p("Rikiya Hohai",4,AIRole.DEFENDER,"Defending",s(62,61,72,67,56,40,44,40,34,41,43,68,60,75,74,79))
    ); }
    public static List<CanonPlayer> roster(String team) { return switch(team) { case "Team X" -> teamX(); case "Team Y" -> teamY(); case "Team W" -> teamW(); case "Team V" -> teamV(); default -> teamZ(); }; }
}
