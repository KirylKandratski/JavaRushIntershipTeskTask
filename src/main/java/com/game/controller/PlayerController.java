package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;

    }

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> showAllPlayers(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                                       @RequestParam(value = "order", defaultValue = "ID") PlayerOrder order,
                                                       @RequestParam(value = "name", required = false) String name,
                                                       @RequestParam(value = "title", required = false) String title,
                                                       @RequestParam(value = "race", required = false) Race race,
                                                       @RequestParam(value = "profession", required = false) Profession profession,
                                                       @RequestParam(value = "after", required = false) Long after,
                                                       @RequestParam(value = "before", required = false) Long before,
                                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        List<Player> playerList = playerService.showAllPlayers(pageSize,pageNumber, order, name, title, race, profession, after, before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel);


        return new ResponseEntity<>(playerList, HttpStatus.OK);
    }


    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> showCountPlayers(@RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                    @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                                    @RequestParam(value = "order", defaultValue = "ID") PlayerOrder order,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "title", required = false) String title,
                                                    @RequestParam(value = "race", required = false) Race race,
                                                    @RequestParam(value = "profession", required = false) Profession profession,
                                                    @RequestParam(value = "after", required = false) Long after,
                                                    @RequestParam(value = "before", required = false) Long before,
                                                    @RequestParam(value = "banned", required = false) Boolean banned,
                                                    @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                    @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                    @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                    @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        List<Player> playerList = playerService.showCountPlayers(pageSize,pageNumber, order, name, title, race, profession, after, before,
                banned,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel);
        Integer count = playerList.size();

        return new ResponseEntity<>(count, HttpStatus.OK);
    }


    @PostMapping("/rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody @Valid Player player) {
        if (player.getName() == null || (player.getName().length() > 12) || (player.getName() == "")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (player.getTitle() == null || (player.getTitle().length() > 30) || (player.getTitle() == "")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ((player.getBirthday() == null) || (player.getBirthday().getTime() < 0) || (player.getRace() == null)
                || (player.getProfession() == null)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if ((player.getExperience() > 10000000) || (player.getExperience() < 0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        enrichPlayer(player);
        playerService.save(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> showPlayer(@PathVariable(name = "id") long id) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Player player = playerService.findOne(id);
        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable(name = "id") long id, @RequestBody Player player) {

        final Player playerOld = playerService.findOne(id);
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (playerOld == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        enrichForUpdatePlayer(player, playerOld);

        if ((player.getName().length() > 12)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ((player.getTitle().length() > 30)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ((player.getBirthday().getTime() < 0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if ((player.getExperience() > 10000000) || (player.getExperience() < 0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        playerService.update(id, player);

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable(name = "id") long id) {
        if (id == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Player player = playerService.findOne(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            playerService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public void enrichPlayer(Player player) {

        Integer level = (int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100);
        player.setLevel(level);

        Integer untilNextLevelPlayer = 50 * (level + 1) * (level + 2) - player.getExperience();
        player.setUntilNextLevel(untilNextLevelPlayer);

        if (player.getBanned() == null) {
            player.setBanned(false);
        }

    }

    public void enrichForUpdatePlayer(Player player, Player playerOld) {
        if ((player.getName() == null) || (player.getName() == "")) {
            player.setName(playerOld.getName());
        }
        if (player.getTitle() == null) {
            player.setTitle(playerOld.getTitle());
        }
        if (player.getRace() == null) {
            player.setRace(playerOld.getRace());
        }
        if (player.getProfession() == null) {
            player.setProfession(playerOld.getProfession());
        }
        if (player.getBirthday() == null) {
            player.setBirthday(playerOld.getBirthday());
        }
        if (player.getBanned() == null) {
            player.setBanned(playerOld.getBanned());
        }
        if (player.getExperience() == null) {

            player.setExperience(playerOld.getExperience());
        }

        enrichPlayer(player);


    }
}
