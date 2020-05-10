package fr.depp.drawme.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.R;
import fr.depp.drawme.databinding.FragmentGameBinding;
import fr.depp.drawme.models.Game;
import fr.depp.drawme.models.InGameInfoWrapper;
import fr.depp.drawme.utils.FragmentHelper;
import fr.depp.drawme.utils.OnBackPressed;
import io.reactivex.rxjava3.disposables.Disposable;

public class GameFragment extends Fragment implements OnBackPressed {

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
        isDrawingInterface = !game.isCurrentPlayer();
        updateView(new InGameInfoWrapper(game.getCurrentPlayer(), game.getWordToGuess(), "")); // Update the first time with local data

        return binding.getRoot();
    }

    private boolean onSubmitWordHandler() {
        FragmentHelper.hideKeyboard(this);
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
        menu.findItem(R.id.action_game_info).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_game_info) {
            new GameStatusDialog().show(getParentFragmentManager(), "test");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        Log.e("GameFragment", "onDestroyView: " );
        super.onDestroyView();
        gameInfoSubscriber.dispose();
    }

    private void updateView(InGameInfoWrapper gameInfo) {
        Game game = Game.getInstance();
        // check if the game is ended
        if (game.getEndMessage() != null) {
            showGameEndedDialog(game.getEndMessage());
            return;
        }

        boolean isPlayerTurn = gameInfo.getCurrentPlayer().equals(game.getLocalPlayerName());
        // if the view needs to be updated = the state of the player changed

        if (isPlayerTurn != isDrawingInterface) {
            isDrawingInterface = isPlayerTurn;

            if (isDrawingInterface) {
                Toasty.normal(requireContext(), game.getWordToGuess(), Toasty.LENGTH_LONG).show();
                binding.colorPicker.setVisibility(View.VISIBLE);
                binding.inputGuessedWord.setVisibility(View.GONE);
                binding.drawingCanvas.setCanDraw(true);
            }
            else {
                binding.colorPicker.setVisibility(View.GONE);
                binding.inputGuessedWord.setVisibility(View.VISIBLE);
                binding.drawingCanvas.setCanDraw(false);
            }
            binding.drawingCanvas.clearCanvas();
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

    private void showGameEndedDialog(String endMessage) {
        new AlertDialog.Builder(requireContext())
                .setTitle("La partie est finie")
                .setMessage(endMessage)
                .setCancelable(false)
                .setNeutralButton("Quitter", (dialog, which) ->
                        FragmentHelper.displayFragment(getParentFragmentManager(), new MainFragment(), false)).show();
    }

    @Override
    public void onDestroy() {
        // remove the player from the game in the database
        Game.getInstance().destroyGame();
        Log.e("GameFragment", "onDestroy: ");

        super.onDestroy();
    }

    @Override
    public void onBackPressed(HandleOnBackPressed callback) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Attention")
                .setMessage("Etes vous sûr de vouloir quitter la partie ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    getParentFragmentManager().beginTransaction().remove(this).commit();
                    callback.onBackPressed(true);
                })
                .setNegativeButton("Non", (dialog, which) -> callback.onBackPressed(false))
                .show();
    }
}