package com.example.quizzapp_badreddineoussaih;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    public static List<Question> getVideoGameQuestions() {
        List<Question> questions = new ArrayList<>();
        
        questions.add(new Question("Who is the main protagonist of the 'Legend of Zelda' series?", 
                new String[]{"Zelda", "Link", "Ganon", "Sheik"}, 1));
        
        questions.add(new Question("Which company developed the 'PlayStation' console?", 
                new String[]{"Nintendo", "Sega", "Sony", "Microsoft"}, 2));
        
        questions.add(new Question("In 'Minecraft', which mob is known for exploding?", 
                new String[]{"Zombie", "Enderman", "Skeleton", "Creeper"}, 3));
        
        questions.add(new Question("What is the best-selling video game of all time (as of 2024)?", 
                new String[]{"Tetris", "Minecraft", "GTA V", "Wii Sports"}, 1));
        
        questions.add(new Question("Which game features the phrase 'Finish Him!'?", 
                new String[]{"Street Fighter", "Tekken", "Mortal Kombat", "SoulCalibur"}, 2));
        
        questions.add(new Question("Who is the creator of Super Mario?", 
                new String[]{"Shigeru Miyamoto", "Hideo Kojima", "Satoshi Tajiri", "Tetsuya Nomura"}, 0));
        
        questions.add(new Question("What is the name of the main character in the 'Halo' series?", 
                new String[]{"Commander Shepard", "Marcus Fenix", "Master Chief", "Doomguy"}, 2));
        
        questions.add(new Question("Which year was the original Nintendo Entertainment System (NES) released in North America?", 
                new String[]{"1983", "1985", "1987", "1989"}, 1));
        
        questions.add(new Question("What is the primary setting of 'BioShock'?", 
                new String[]{"Rapture", "Columbia", "Liberty City", "Vault 101"}, 0));
        
        questions.add(new Question("Which character is the mascot of SEGA?", 
                new String[]{"Mario", "Sonic", "Alex Kidd", "Knuckles"}, 1));

        questions.add(new Question("In 'Pac-Man', what is the name of the orange ghost?", 
                new String[]{"Blinky", "Pinky", "Inky", "Clyde"}, 3));

        questions.add(new Question("Which game is widely considered the first 'Battle Royale'?", 
                new String[]{"Fortnite", "PUBG", "H1Z1", "Apex Legends"}, 1));

        questions.add(new Question("What is the name of the kingdom where Mario lives?", 
                new String[]{"Hyrule", "Mushroom Kingdom", "Azeroth", "Dream Land"}, 1));

        questions.add(new Question("Which studio developed 'The Last of Us'?", 
                new String[]{"Insomniac Games", "Naughty Dog", "Santa Monica Studio", "Guerrilla Games"}, 1));

        questions.add(new Question("What is the name of the main currency in 'League of Legends'?", 
                new String[]{"Gold", "Mana", "V-Bucks", "RP"}, 0));

        questions.add(new Question("Which console was known as the 'Project Scorpio' during development?", 
                new String[]{"Xbox 360", "Xbox One X", "Xbox Series X", "PlayStation 4 Pro"}, 1));

        questions.add(new Question("Who is the 'Goddess of Light' in the 'Kid Icarus' series?", 
                new String[]{"Palutena", "Medusa", "Viridi", "Pandora"}, 0));

        questions.add(new Question("In 'The Witcher 3', what is Geralt's horse called?", 
                new String[]{"Epona", "Roach", "Aggro", "Shadowfax"}, 1));

        questions.add(new Question("Which game popularized the 'Fatality' finishing move?", 
                new String[]{"Mortal Kombat", "Killer Instinct", "Doom", "Quake"}, 0));

        questions.add(new Question("What was the first commercial video game?", 
                new String[]{"Pong", "Computer Space", "Pac-Man", "Space Invaders"}, 1));

        return questions;
    }
}