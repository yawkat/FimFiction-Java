package at.yawk.fimfiction.data;

import at.yawk.fimfiction.net.NetUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import java.net.URL;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for characters that appear in Fimfiction stories. Most characters should be part of #DefaultCharacter
 * but they are added quite frequently so this can not be assumed for all FimCharacter instances.
 *
 * @author Jonas Konrad (yawkat)
 */
public interface FimCharacter extends Identifiable {
    /**
     * The (currently) unique ID Fimfiction uses to identify this character.
     */
    int getFimfictionId();

    /**
     * The URL to the image of this character.
     */
    @Nonnull
    URL getImageUrl();

    /**
     * Default characters.
     */
    public static enum DefaultCharacter implements FimCharacter {
        TWILIGHT_SPARKLE(7, "twilight_sparkle"),
        RAINBOW_DASH(8, "rainbow_dash"),
        PINKIE_PIE(9, "pinkie_pie"),
        APPLEJACK(10, "applejack"),
        RARITY(11, "rarity"),
        FLUTTERSHY(12, "fluttershy"),
        SPIKE(16, "spike"),
        MAIN_6(74, "main_6"),
        TWILICORN(94, "twilicorn"),
        APPLE_BLOOM(13, "apple_bloom"),
        SCOOTALOO(14, "scootaloo"),
        SWEETIE_BELLE(15, "sweetie_belle"),
        CUTIE_MARK_CRUSADERS(75, "cmc"),
        BABS_SEED(84, "babs_seed"),
        PRINCESS_CELESTIA(17, "celestia"),
        PRINCESS_LUNA(18, "princess_luna"),
        NIGHTMARE_MOON(54, "nightmare_moon"),
        GILDA(19, "gilda"),
        ZECORA(20, "zecora"),
        TRIXIE(21, "trixie"),
        CHERILEE(30, "cherilee"),
        THE_MAYOR(31, "the_mayor"),
        HOITY_TOITY(32, "hoity_toity"),
        PHOTO_FINISH(33, "photo_finish"),
        SAPPHIRE_SHORES(34, "sapphire_shores"),
        SPITFIRE(35, "spitfire"),
        SOARIN(36, "soarin"),
        PRINCE_BLUEBLOOD(37, "prince_blueblood"),
        LITTLE_STRONGHEART(38, "little_strongheart"),
        DISCORD(53, "discord"),
        MARE_DO_WELL(58, "mare_do_well"),
        FANCYPANTS(60, "fancypants"),
        DARING_DO(63, "daring_do"),
        FLIM_AND_FLAM("flim_and_flam", 65, "flimflam"),
        CRANKY_DOODLE_DONKEY(66, "cranky_doodle"),
        MATILDA(67, "matilda"),
        MR_CAKE(68, "mr_cake"),
        MRS_CAKE(69, "mrs_cake"),
        IRON_WILL("iron_will", 71, "ironwill"),
        PRINCESS_CADANCE(72, "cadance"),
        SHINING_ARMOR(73, "shining_armor"),
        WONDERBOLTS(76, "wonderbolts"),
        DIAMOND_DOGS(77, "diamond_dogs"),
        QUEEN_CHRYSALIS(78, "queen_chrysalis"),
        KING_SOMBRA(83, "king_sombra"),
        CRYSTAL_PONIES(86, "crystal_ponies"),
        LIGHTNING_DUST(89, "lightning_dust"),
        BIG_MACINTOSH(22, "big_mac"),
        GRANNY_SMITH(23, "granny_smith"),
        BRAEBURN(24, "braeburn"),
        DIAMOND_TIARA(25, "diamond_tiara"),
        SILVER_SPOON(26, "silver_spoon"),
        TWIST(27, "twist"),
        SNIPS(28, "snips"),
        SNAILS(29, "snails"),
        PIPSQUEAK(55, "pipsqueak"),
        FEATHERWEIGHT(87, "featherweight"),
        ANGEL(39, "angel"),
        WINONA(40, "winona"),
        OPALESCENCE(41, "opalescence"),
        GUMMY(42, "gummy"),
        OWLOWISCIOUS(43, "owlowiscious"),
        PHILOMENA(44, "philomena"),
        TANK(59, "tank"),
        DERPY_HOOVES(45, "derpy_hooves"),
        LYRA(46, "lyra"),
        BONBON(47, "bon_bon"),
        DJ_P0N3(48, "dj_pon3"),
        CARAMEL(50, "caramel"),
        DOCTOR_WHOOVES(51, "doctor_whooves"),
        OCTAVIA(52, "octavia"),
        BERRY_PUNCH(56, "berry_punch"),
        CARROT_TOP(57, "carrot_top"),
        FLEUR_DE_LIS(61, "fleur_de_lis"),
        COLGATE(64, "colgate"),
        DINKY_HOOVES(70, "dinky"),
        THUNDERLANE(79, "thunderlane"),
        FLITTER_AND_CLOUDCHASER(80, "flitter_and_cloudchaser"),
        RUMBLE(81, "rumble"),
        ROSELUCK(82, "roseluck"),
        CHANGELINGS(85, "changelings"),
        NOTEWORTHY(88, "noteworthy"),
        NURSE_REDHEART(90, "nurse_red_heart"),
        FLOWER_PONIES(91, "flower_ponies"),
        RAINDROPS(92, "raindrops"),
        SPA_PONIES(93, "spa_ponies"),
        ORIGINAL_CHARACTER(49, "oc"),
        OTHER(62, "other"),
        CAKE_TWINS(98, "cake_twins"),
        CHERRY_JUBILEE(97, "cherry_jubilee"),
        FLASH_SENTRY(100, "flash_sentry"),
        PIE_SISTERS(96, "pie_sisters"),
        SPARKLER(99, "sparkler"),
        SUNSET_SHIMMER(95, "sunset_shimmer"),
        CLOUDKICKER(101, "cloudkicker"),
        MANE_IAC("mane_iac", 102, "mane-iac"),
        POWER_PONIES(103, "power_ponies");

        private static final DefaultCharacter[] defaultCharactersByFimfictionId = new DefaultCharacter[104];

        static {
            for (DefaultCharacter character : DefaultCharacter.values()) {
                defaultCharactersByFimfictionId[character.getFimfictionId()] = character;
            }
        }

        static { IdentifiableMapper.addMapping(FimCharacter.class, values()); }

        /**
         * Returns a FimCharacter for the ID generated by Identifiable#getId. This does not only return
         * DefaultCharacters but might also return encoded, generic ones.
         */
        @Nullable
        public static FimCharacter forId(@Nonnull String id) {
            if (id.startsWith("generic:")) {
                List<String> parts = Splitter.on(':').limit(3).splitToList(id);
                if (parts.size() == 3) {
                    try {
                        return getOrCreateCharacter(Integer.parseInt(parts.get(1)), new URL(parts.get(2)));
                    } catch (Exception ignored) {}
                }
            }
            return IdentifiableMapper.findIdentifiable(FimCharacter.class, id);
        }

        /**
         * Returns a default character for the given fimfiction ID.
         */
        @Nullable
        public static DefaultCharacter forFimfictionId(int fimfictionId) {
            return fimfictionId >= 0 && fimfictionId < defaultCharactersByFimfictionId.length ?
                    defaultCharactersByFimfictionId[fimfictionId] :
                    null;
        }

        /**
         * Returns a FimCharacter for the given id and image URL. This might be a DefaultCharacter or a generic one
         * if the character is unknown. In the case of generated instances, the getId method will return an encoded
         * String that can be understood by forId.
         */
        @Nonnull
        public static FimCharacter getOrCreateCharacter(final int fimfictionId, @Nonnull final URL imageUrl) {
            Preconditions.checkNotNull(imageUrl);
            FimCharacter defaultCharacter = forFimfictionId(fimfictionId);
            if (defaultCharacter == null) {
                return new FimCharacter() {
                    @Override
                    public int getFimfictionId() {
                        return fimfictionId;
                    }

                    @Nonnull
                    @Override
                    public URL getImageUrl() {
                        return imageUrl;
                    }

                    @Nonnull
                    @Override
                    public String getId() {
                        return "generic:" + getFimfictionId() + ":" + getImageUrl();
                    }
                };
            } else {
                return defaultCharacter;
            }
        }

        @Nonnull private final String id;
        private final int fimfictionId;
        @Nonnull private final URL imageUrl;

        private DefaultCharacter(int fimfictionId, @Nonnull String imageId) {
            this(imageId, fimfictionId, imageId);
        }

        private DefaultCharacter(@Nonnull String id, int fimfictionId, @Nonnull String imageId) {
            this.id = id;
            this.fimfictionId = fimfictionId;
            this.imageUrl = NetUtil.createUrlNonNull("http://www.fimfiction-static.net/images/characters/" +
                                                     imageId +
                                                     ".png");
        }

        @Nonnull
        @Override
        public String getId() {
            return id;
        }

        @Override
        public int getFimfictionId() {
            return fimfictionId;
        }

        @Nonnull
        @Override
        public URL getImageUrl() {
            return imageUrl;
        }
    }
}
