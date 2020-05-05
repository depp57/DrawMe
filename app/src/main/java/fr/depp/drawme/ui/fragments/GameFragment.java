package fr.depp.drawme.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentGameBinding;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.InGameInfoWrapper;
import io.reactivex.rxjava3.disposables.Disposable;

public class GameFragment extends Fragment {

    private FragmentGameBinding binding;
    private Disposable gameInfoSubscriber;
    private boolean isDrawingInterface;
    private String lastGuessedWord;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // without this line, onCreateOptionsMenu() isn't called
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        binding.colorPicker.setCallback(color -> binding.drawingCanvas.setColor(color));
        binding.inputGuessedWord.setOnEditorActionListener((view, aId, e) -> onSubmitWordHandler());

        binding.textSwitcherGuessWords.setInAnimation(requireContext(), android.R.anim.slide_in_left);
        binding.textSwitcherGuessWords.setOutAnimation(requireContext(), android.R.anim.slide_out_right);

        Game game = Game.getInstance();
        lastGuessedWord = "";
        gameInfoSubscriber = game.inGameInfoSubject.subscribe(this::updateView);
        updateView(new InGameInfoWrapper(game.getCurrentPlayer(), game.getWordToGuess(), "")); // Update the first time with local data
        isDrawingInterface = !game.getCurrentPlayer().equals(game.getLocalPlayerName());

        return binding.getRoot();
    }

    private boolean onSubmitWordHandler() {
        String word = binding.inputGuessedWord.getText().toString();
        if (word.length() > 0) {
            binding.inputGuessedWord.getText().clear();
            Game.getInstance().updateGuessedWord(word);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_account).setVisible(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gameInfoSubscriber.dispose();
    }

    private void updateView(InGameInfoWrapper gameInfo) {
        Game game = Game.getInstance();
        boolean isPlayerTurn = gameInfo.getCurrentPlayer().equals(game.getLocalPlayerName());
        // if the view needs to be updated = the state of the player changed

        if (isPlayerTurn != isDrawingInterface) {
            isDrawingInterface = isPlayerTurn;

            if (isDrawingInterface) {
                binding.colorPicker.setVisibility(View.VISIBLE);
                binding.inputGuessedWord.setVisibility(View.GONE);
                binding.drawingCanvas.setCanDraw(true);
            }
            else {
                binding.colorPicker.setVisibility(View.GONE);
                binding.inputGuessedWord.setVisibility(View.VISIBLE);
                binding.drawingCanvas.setCanDraw(false);
            }
            binding.drawingCanvas.clearCanvas(); //TODO voir pour appeler updateView quand la partie commence car la ca clear chelou tu connais
        }

        if (!isPlayerTurn) {
            binding.drawingCanvas.updateDrawing(gameInfo.getLastPath());
        }

        String wordFetched = gameInfo.getLastGuessedWord();
        if (!lastGuessedWord.equals(wordFetched)) {
            binding.textSwitcherGuessWords.setText(wordFetched);
            lastGuessedWord = wordFetched;
        }
    }
}