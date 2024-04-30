package net.gazeplay.games.sprintFinish;

import net.gazeplay.GameCategories;
import net.gazeplay.GameSpec;
import net.gazeplay.GameSpecSource;
import net.gazeplay.GameSummary;

public class SprintFinishMouseSpecSource implements GameSpecSource {

    @Override
    public GameSpec getGameSpec() {
        return new GameSpec(
            GameSummary.builder().nameCode("SprintFinishMouse").gameThumbnail("SprintToTheFinish")
                .category(GameCategories.Category.ACTION_REACTION).build(),
            new SprintFinishVariantGenerator(), new SprintFinishMouseLauncher());
    }
}
