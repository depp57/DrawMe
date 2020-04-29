const functions = require('firebase-functions');


exports.onGameUpdate = functions.firestore
    .document('games/{name}')
    .onUpdate((change, context) => {
      const data = change.after.data();
      const players = data.players;

      // if the room is empty, delete it
      if (Object.keys(players).length === 0) {
            change.after.ref.delete();
      }

      return true;
    }
);
