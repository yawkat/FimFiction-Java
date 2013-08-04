package at.yawk.fimficiton.operation;

import at.yawk.fimficiton.Character;
import at.yawk.fimficiton.SearchParameters.Order;
import at.yawk.fimficiton.Story.Category;
import at.yawk.fimficiton.Story.ContentRating;

/**
 * Various helper methods used for building search request GET parameters.
 * 
 * @author Yawkat
 */
class SearchRequestUtil {
    private SearchRequestUtil() {}
    
    static String orderToParameterString(final Order order) {
        switch (order) {
        case COMMENT_COUNT:
            return "comments";
        case FIRST_POSTED_DATE:
            return "latest";
        case HOT:
            return "heat";
        case RATING:
            return "top";
        case UPDATE_DATE:
            return "updated";
        case VIEW_COUNT:
            return "views";
        case WORD_COUNT:
            return "words";
        default:
            throw new IllegalArgumentException();
        }
    }
    
    static String categoryToParameterString(final Category category) {
        return category.toString().toLowerCase();
    }
    
    static int getContentRatingId(final ContentRating contentRating) {
        if (contentRating == null) {
            return -1;
        }
        switch (contentRating) {
        case EVERYONE:
            return 0;
        case MATURE:
            return 2;
        case TEEN:
            return 1;
        default:
            throw new IllegalArgumentException();
        }
    }
    
    static int getCharacterId(final Character character) {
        switch (character) {
        case TWILIGHT_SPARKLE:
            return 7;
        case RAINBOW_DASH:
            return 8;
        case PINKIE_PIE:
            return 9;
        case APPLEJACK:
            return 10;
        case RARITY:
            return 11;
        case FLUTTERSHY:
            return 12;
        case SPIKE:
            return 16;
        case MAIN_6:
            return 74;
        case TWILICORN:
            return 94;
        case APPLE_BLOOM:
            return 13;
        case SCOOTALOO:
            return 14;
        case SWEETIE_BELLE:
            return 15;
        case CUTIE_MARK_CRUSADERS:
            return 75;
        case BABS_SEED:
            return 84;
        case PRINCESS_CELESTIA:
            return 17;
        case PRINCESS_LUNA:
            return 18;
        case NIGHTMARE_MOON:
            return 54;
        case GILDA:
            return 19;
        case ZECORA:
            return 20;
        case TRIXIE:
            return 21;
        case CHERILEE:
            return 30;
        case THE_MAYOR:
            return 31;
        case HOITY_TOITY:
            return 32;
        case PHOTO_FINISH:
            return 33;
        case SAPPHIRE_SHORES:
            return 34;
        case SPITFIRE:
            return 35;
        case SOARIN:
            return 36;
        case PRINCE_BLUEBLOOD:
            return 37;
        case LITTLE_STRONGHEART:
            return 38;
        case DISCORD:
            return 53;
        case MARE_DO_WELL:
            return 58;
        case FANCYPANTS:
            return 60;
        case DARING_DO:
            return 63;
        case FLIM_AND_FLAM:
            return 65;
        case CRANKY_DOODLE_DONKEY:
            return 66;
        case MATILDA:
            return 67;
        case MR_CAKE:
            return 68;
        case MRS_CAKE:
            return 69;
        case IRON_WILL:
            return 71;
        case PRINCESS_CADENCE:
            return 72;
        case SHINING_ARMOR:
            return 73;
        case WONDERBOLTS:
            return 76;
        case DIAMOND_DOGS:
            return 77;
        case QUEEN_CHRYSALIS:
            return 78;
        case KING_SOMBRA:
            return 83;
        case CRYSTAL_PONIES:
            return 86;
        case LIGHTNING_DUST:
            return 89;
        case BIG_MACINTOSH:
            return 22;
        case GRANNY_SMITH:
            return 23;
        case BRAEBURN:
            return 24;
        case DIAMOND_TIARA:
            return 25;
        case SILVER_SPOON:
            return 26;
        case TWIST:
            return 27;
        case SNIPS:
            return 28;
        case SNAILS:
            return 29;
        case PIPSQUEAK:
            return 55;
        case FEATHERWEIGHT:
            return 87;
        case ANGEL:
            return 39;
        case WINONA:
            return 40;
        case OPALESCENCE:
            return 41;
        case GUMMY:
            return 42;
        case OWLOWISCIOUS:
            return 43;
        case PHILOMENA:
            return 44;
        case TANK:
            return 59;
        case DERPY_HOOVES:
            return 45;
        case LYRA:
            return 46;
        case BONBON:
            return 47;
        case DJ_P0N3:
            return 48;
        case CARAMEL:
            return 50;
        case DOCTOR_WHOOVES:
            return 51;
        case OCTAVIA:
            return 52;
        case BERRY_PUNCH:
            return 56;
        case CARROT_TOP:
            return 57;
        case FLEUR_DE_LIS:
            return 61;
        case COLGATE:
            return 64;
        case DINKY_HOOVES:
            return 70;
        case THUNDERLANE:
            return 79;
        case FLITTER_AND_CLOUDCHASER:
            return 80;
        case RUMBLE:
            return 81;
        case ROSELUCK:
            return 82;
        case CHANGELINGS:
            return 85;
        case NOTEWORTHY:
            return 88;
        case NURSE_REDHEART:
            return 90;
        case FLOWER_PONIES:
            return 91;
        case RAINDROPS:
            return 92;
        case SPA_PONIES:
            return 93;
        case ORIGINAL_CHARACTER:
            return 49;
        case OTHER:
            return 62;
        case CAKE_TWINS:
            return 98;
        case CHERRY_JUBILEE:
            return 97;
        case FLASH_SENTRY:
            return 100;
        case PIE_SISTERS:
            return 96;
        case SPARKLER:
            return 99;
        case SUNSET_SHIMMER:
            return 95;
        default:
            throw new IllegalArgumentException();
        }
    }
}
