package fr.depp.drawme.utils;

public abstract class WordsToGuess {

    public static String getRandomWord() {
        final String[] WORDS =
                {
                        "Montre",
                        "Pêcheur",
                        "Écureuil",
                        "Baleine",
                        "Souris",
                        "Hippocampe",
                        "Ruche",
                        "Ordinateur",
                        "Niche",
                        "Mamie",
                        "Dinosaure",
                        "Pêcher",
                        "Limousine",
                        "Chevalier",
                        "Oeuf de Pâques",
                        "Bonhomme de neige",
                        "Poireau",
                        "Cravate",
                        "Robe de mariée",
                        "Pyjama",
                        "Bague",
                        "Varicelle",
                        "Lion",
                        "Barbe",
                        "Pizza",
                        "Ciseaux",
                        "Trousse",
                        "Rouge à lèvres",
                        "Écharpe",
                        "Chaise",
                        "Cadeau",
                        "Ballon",
                        "Lunettes",
                        "Arrosoir",
                        "Index",
                        "Genou",
                        "Domino",
                        "Pirate",
                        "Magicien",
                        "Père Noël",
                        "Momie",
                        "Cirque",
                        "Fourchette",
                        "Poubelle",
                        "Crocodile",
                        "Glace",
                        "Coffre-fort",
                        "Igloo",
                        "Tour Eiffel",
                        "Notre Dame",
                        "Prise électrique",
                       " Calendrier",
                        "Cheminée",
                       " Chaussette",
                        "Jeu de cartes",
                       " Pétanque"
                };
        return WORDS[(int) (Math.random() * WORDS.length)];
    }
}
