package project.gamepage.service.game.fly_in_the_web;

import project.gamepage.data.model.game.fly_in_the_web.FITW;
import project.gamepage.data.model.user.User;
import project.gamepage.data.model.game.PvC;
import project.gamepage.data.model.game.PvP;
import project.gamepage.data.model.game.fly_in_the_web.Piece_FITW;
import project.gamepage.data.model.game.fly_in_the_web.Pieces_FITW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gamepage.service.invitations.InvitationService;
import project.gamepage.service.UserService;

import java.util.*;

@Service
public class GameService_FITW {
    List<PvP<FITW>> pvpList;
    List<PvC<FITW>> pvcList;
    UserService userService;
    InvitationService invitationService;
    Random random;
    private final short PVP = 1;
    private final short PVS = 2;
    private final short PVF = 3;

    @Autowired
    public GameService_FITW(InvitationService invitationService, UserService userService) {
        this.invitationService = invitationService;
        this.userService = userService;
        this.pvpList = new ArrayList<>();
        this.pvcList = new ArrayList<>();
        this.random = new Random();
    }

    public PvP<FITW> getPvP(String username) {
        for (PvP<FITW> pvp : pvpList) {
            if (pvp.getUser1().equals(username) || (pvp.getUser2() != null && pvp.getUser2().equals(username))) {
                return pvp;
            }
        }

        return createPvP(username);
    }

    public PvC<FITW> getPvC(String username) {
        for (PvC<FITW> pvc : pvcList) {
            if (pvc.getUser().equals(username)) {
                return pvc;
            }
        }

        return createPvC(username);
    }

    public PvP<FITW> createPvP(String username) {
        PvP<FITW> newPvP = new PvP<>(username, null, new FITW());
        pvpList.add(newPvP);
        return newPvP;
    }

    public PvC<FITW> createPvC(String username) {
        PvC<FITW> newPvC = new PvC<>(username, new FITW());
        pvcList.add(newPvC);
        return newPvC;
    }

    public PvP<FITW> joinLobby(String inviter, String invited, String game) {
        for (PvP<FITW> pvP : pvpList) {
            if (pvP.getUser1().equals(inviter)) {
                pvpList.removeIf(i -> i.getUser1().equals(invited));
                pvP.setUser2(invited);
                invitationService.removeInvitation(invited, inviter, game);
                return pvP;
            }
        }
        return null;
    }

    public PvP<FITW> quitLobby(String name) {
        PvP<FITW> pvp = getPvP(name);
        if (pvp.getUser1() != null && pvp.getUser2() != null) {
            pvpList.remove(pvp);
        }
        return getPvP(name);
    }


    //------------------------FITW--------------------------

    public int moveVsComputer(int from, int to, FITW boardFITW) {
        if (whichPiece(from, boardFITW) == boardFITW.getFly()) {
            moveFly(from, to, boardFITW);
        }
        for (Piece_FITW p : boardFITW.getSpider()) {
            if (whichPiece(from, boardFITW) == p) {
                moveSpider(from, to, p, boardFITW);
            }
        }

        return whoWon(boardFITW);
    }

    public int moveVsPlayer(int from, int to, FITW boardFITW, String username) {
        PvP<FITW> pvP = getPvP(username);
        if (whichPiece(from, boardFITW) == boardFITW.getFly()) {
            if (pvP.getUser1().equals(username)) {
                moveFly(from, to, boardFITW);
            }
        }
        for (Piece_FITW p : boardFITW.getSpider()) {
            if (whichPiece(from, boardFITW) == p) {
                if (pvP.getUser2().equals(username)) {
                    moveSpider(from, to, p, boardFITW);
                }
            }
        }

        return whoWon(boardFITW);
    }

    public boolean randomMoveFly(FITW boardFITW) {
        Piece_FITW fly = boardFITW.getFly();

        int randomConnection = random.nextInt(boardFITW.getField()[fly.location].getConnections().size());
        int randomField = boardFITW.getField()[fly.location].getConnections().get(randomConnection).getNumber();

        if (boardFITW.isGameRunning) {
            if(isMoveValid(boardFITW.getFly().location, randomField, boardFITW)) {
                moveFly(boardFITW.getFly().location, randomField, boardFITW);
                return true;
            } else {
                randomMoveFly(boardFITW);
            }
        }
        return false;
    }

    public boolean randomMoveSpider(FITW boardFITW) {
        Piece_FITW randomSpider = boardFITW.getSpider()[random.nextInt(boardFITW.getSpider().length)];

        int randomConnection = random.nextInt(boardFITW.getField()[randomSpider.location].getConnections().size());
        int randomField = boardFITW.getField()[randomSpider.location].getConnections().get(randomConnection).getNumber();

        if (boardFITW.isGameRunning) {
            if(isMoveValid(randomSpider.location, randomField, boardFITW)) {
                moveSpider(randomSpider.location, randomField, randomSpider, boardFITW);
                return true;
            } else {
                randomMoveSpider(boardFITW);
            }
        }
        return false;
    }

    public boolean isMoveValid(int from, int to, FITW boardFITW) {
        boolean correctPiece = false;
        if(boardFITW.isFlysTurn) {
            if (from == boardFITW.getFly().location) {
                correctPiece = true;
            } else return false;
        } else {
            for (int i = 0; i < boardFITW.getSpider().length; i++) {
                if (from == boardFITW.getSpider()[i].location) correctPiece = true;
            }
        }

        boolean areFieldsConnected = false;
        boolean isFromFieldEmpty = boardFITW.getField()[from].getPiece() == Pieces_FITW.EMPTY;
        boolean isToFieldEmpty = boardFITW.getField()[to].getPiece() == Pieces_FITW.EMPTY;

        for (int i = 0; i < boardFITW.getField()[from].getConnections().size(); i++) {
            if (boardFITW.getField()[from].getConnections().get(i) == boardFITW.getField()[to]) {
                areFieldsConnected = true;
            }
        }

        return isGameRunning(boardFITW) && !isFromFieldEmpty && isToFieldEmpty && correctPiece && areFieldsConnected;
    }

    public int[] getPositions(FITW boardFITW) {
        int[] loc = new int[boardFITW.getSpider().length + 1];
        loc[0] = boardFITW.getFly().location;
        for (int i = 1; i < boardFITW.getSpider().length + 1; i++) {
            loc[i] = boardFITW.getSpider()[i-1].location;
        }

        return loc;
    }

    public short getGameMode(FITW boardFITW) {
        return boardFITW.gameMode;
    }

    public HashMap<Integer, ArrayList<Integer>> getConnections(FITW boardFITW) {
        HashMap<Integer, ArrayList<Integer>> connections = new HashMap<>();
        for (int i = 0; i < boardFITW.getField().length; i++) {
            ArrayList<Integer> connectionOfField = new ArrayList<>();
            for (int j = 0; j < boardFITW.getField()[i].getConnections().size(); j++) {
                connectionOfField.add(boardFITW.getField()[i].getConnections().get(j).getNumber());
                connections.put(i, connectionOfField);
            }
        }

        return connections;
    }


    //---------------- Child methods of Main methods listed above ----------------
    public void moveFly(int from, int to, FITW boardFITW) {
        if (isMoveValid(from, to, boardFITW)) {
            boardFITW.getField()[to].setPiece(boardFITW.getField()[from].getPiece());
            boardFITW.getField()[from].setPiece(Pieces_FITW.EMPTY);
            boardFITW.getFly().location = to;
            boardFITW.isFlysTurn = false;
            boardFITW.flyStepsDone++;
        }
    }

    public void moveSpider(int from, int to, Piece_FITW p, FITW boardFITW) {
        if (isMoveValid(from, to, boardFITW)) {
            boardFITW.getField()[to].setPiece(boardFITW.getField()[from].getPiece());
            boardFITW.getField()[from].setPiece(Pieces_FITW.EMPTY);
            p.location = to;
            boardFITW.isFlysTurn = true;
            boardFITW.spiderStepsDone++;
        }
    }

    public Piece_FITW whichPiece(int location, FITW boardFITW) {
        Piece_FITW pieceToReturn = null;
        for (Piece_FITW p: boardFITW.getSpider()) {
            if (p.location == location) pieceToReturn = p;
        }
        if (boardFITW.getFly().location == location) pieceToReturn = boardFITW.getFly();

        return pieceToReturn;
    }

    public boolean isGameRunning(FITW boardFITW) {
        if (
                boardFITW.getFly().location == 0 ||
                        boardFITW.getFly().location == 5 ||
                        boardFITW.getFly().location == 10 ||
                        boardFITW.getFly().location == 14 ||
                        boardFITW.getFly().location == 18 ||
                        boardFITW.getFly().location == 22
        ) {
            boardFITW.isGameRunning = false;
            boardFITW.pieceWon = 1;
        }

        int availableFields = getAvailableFields(boardFITW);
        if (availableFields == 0) {
            boardFITW.isGameRunning = false;
            boardFITW.pieceWon = 2;
        }
        return boardFITW.isGameRunning;
    }

    private int getAvailableFields(FITW boardFITW) {
        int availableFields = 0;
        for (int i = 0; i < boardFITW.getField()[boardFITW.getFly().location].getConnections().size(); i++) {
            if(
                    boardFITW.getField()[boardFITW.getFly().location].getConnections().get(i).getPiece() == Pieces_FITW.EMPTY
            ) availableFields++;
        }
        return availableFields;
    }

    public boolean isFlysTurn(FITW boardFITW) {
        return boardFITW.isFlysTurn;
    }


    public boolean newGamePvP(String username) {
        PvP<FITW> pvp = getPvP(username);

        pvp.getBoard().gameMode = PVP;
        pvp.getBoard().isGameRunning = true;
        pvp.getBoard().pieceWon = 0;
        pvp.getBoard().isFlysTurn = true;
        pvp.getBoard().flyStepsDone = 0;
        pvp.getBoard().spiderStepsDone = 0;

        return true;
    }

    public boolean newGamePvC(String gameMode, String username) {
        PvC<FITW> pvc = getPvC(username);
        switch (gameMode) {
            case "pvs": {
                pvc.setBoard(new FITW());
                pvc.getBoard().gameMode = PVS;
                pvc.getBoard().isGameRunning = true;
                pvc.getBoard().pieceWon = 0;
                pvc.getBoard().isFlysTurn = true;
                pvc.getBoard().flyStepsDone = 0;
                pvc.getBoard().spiderStepsDone = 0;
                return true;
            }
            case "pvf": {
                pvc.setBoard(new FITW());
                pvc.getBoard().gameMode = PVF;
                pvc.getBoard().isGameRunning = true;
                pvc.getBoard().pieceWon = 0;
                pvc.getBoard().isFlysTurn = false;
                pvc.getBoard().flyStepsDone = 0;
                pvc.getBoard().spiderStepsDone = 0;
                return true;
            }
            default: return false;
        }
    }

    public boolean getIsGameRunning(FITW boardFITW) {
        return boardFITW.isGameRunning;
    }


    /**
     * pieceWon's value is 0 if no one won yet
     *                      1 if fly won
     *                      2 if spiders won
     * @return value of pieceWon
     */
    public int whoWon(FITW boardFITW) {
        isGameRunning(boardFITW);
        return boardFITW.pieceWon;
    }

    public int getFlyStepsDone(FITW boardFITW) {
        return boardFITW.flyStepsDone;
    }

    public int getSpiderStepsDone(FITW boardFITW) {
        return boardFITW.spiderStepsDone;
    }

    public int gameOver(PvP<FITW> pvp) {
        if (pvp.isOver()) return whoWon(pvp.getBoard());

        User user1 = userService.findByUsername(pvp.getUser1());
        User user2 = userService.findByUsername(pvp.getUser2());

        if (whoWon(pvp.getBoard()) == 1) {
            user1.setMovesDone(user1.getMovesDone() + pvp.getBoard().flyStepsDone);
            user1.setGamesPlayed(user1.getGamesPlayed() + 1);
            user1.setGamesWon(user1.getGamesWon() + 1);

            user2.setMovesDone(user2.getMovesDone() + pvp.getBoard().spiderStepsDone);
            user2.setGamesPlayed(user2.getGamesPlayed() + 1);
        } else if (whoWon(pvp.getBoard()) == 2) {
            user2.setMovesDone(user2.getMovesDone() + pvp.getBoard().spiderStepsDone);
            user2.setGamesPlayed(user2.getGamesPlayed() + 1);
            user2.setGamesWon(user2.getGamesWon() + 1);

            user1.setMovesDone(user1.getMovesDone() + pvp.getBoard().flyStepsDone);
            user1.setGamesPlayed(user1.getGamesPlayed() + 1);
        }

        userService.update(user1);
        userService.update(user2);
        pvp.setOver(true);

        return whoWon(pvp.getBoard());
    }
}