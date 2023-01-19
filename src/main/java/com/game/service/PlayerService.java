package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> showAllPlayers(Integer pageSize, Integer pageNumber, PlayerOrder order, String name, String title, Race race, Profession profession,
                                       Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                       Integer minLevel, Integer maxLevel) {

        return playerRepository.findAll()
                .stream()
                .filter(myPredicate(name,
                        title,
                        race,
                        profession,
                        after,
                        before,
                        banned,
                        minExperience,
                        maxExperience,
                        minLevel,
                        maxLevel))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .sorted(new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        if (order.getFieldName().toLowerCase() == "id") {
                            return o1.getId().compareTo(o2.getId());
                        } else if (order.getFieldName().toLowerCase() == "name") {
                            return o1.getName().compareTo(o2.getName());
                        } else if (order.getFieldName().toLowerCase() == "experience") {
                            return o1.getExperience().compareTo(o2.getExperience());
                        } else if (order.getFieldName().toLowerCase() == "birthday") {
                            return o1.getBirthday().compareTo(o2.getBirthday());
                        } else if (order.getFieldName().toLowerCase() == "level") ;
                        return o1.getLevel().compareTo(o2.getLevel());
                    }
                })
                .collect(Collectors.toList());
    }


    public Predicate<Player> myPredicate(String name, String title, Race race, Profession profession, Long after, Long before,
                                         Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel,
                                         Integer maxLevel) {
        Predicate<Player> playerPredicate = player -> true;

        if (name != null) {
            Predicate<Player> testerName = player -> player.getName().toLowerCase().contains(name);
            playerPredicate = playerPredicate.and(testerName);
        }
        if (title != null) {
            Predicate<Player> testerTitle = player -> player.getTitle().toLowerCase().contains(title);
            playerPredicate = playerPredicate.and(testerTitle);
        }
        if (race != null) {
            Predicate<Player> testerRace = player -> player.getRace().equals(race);
            playerPredicate = playerPredicate.and(testerRace);
        }
        if (profession != null) {
            Predicate<Player> testerProfession = player -> player.getProfession().equals(profession);
            playerPredicate = playerPredicate.and(testerProfession);
        }
        if (after != null) {
            Date afterDate = new Date(after);
            Predicate<Player> testerAfterBirthday = player -> player.getBirthday().after(afterDate);
            playerPredicate = playerPredicate.and(testerAfterBirthday);
        }
        if (before != null) {
            Date beforeDate = new Date(before);
            Predicate<Player> testerBeforeBirthday = player -> player.getBirthday().before(beforeDate);
            playerPredicate = playerPredicate.and(testerBeforeBirthday);
        }
        if (banned != null) {
            Predicate<Player> testerBanned = player -> player.getBanned().equals(banned);
            playerPredicate = playerPredicate.and(testerBanned);
        }
        if (minExperience != null) {
            Predicate<Player> testerMinExperience = player -> player.getExperience() >= minExperience;
            playerPredicate = playerPredicate.and(testerMinExperience);
        }
        if (maxExperience != null) {
            Predicate<Player> testerMaxExperience = player -> player.getExperience() <= maxExperience;
            playerPredicate = playerPredicate.and(testerMaxExperience);
        }
        if (minLevel != null) {
            Predicate<Player> testerMinLevel = player -> player.getLevel() >= minLevel;
            playerPredicate = playerPredicate.and(testerMinLevel);
        }
        if (maxLevel != null) {
            Predicate<Player> testerMaxLevel = player -> player.getLevel() <= maxLevel;
            playerPredicate = playerPredicate.and(testerMaxLevel);
        }

        return playerPredicate;
    }

    public List<Player> showCountPlayers(Integer pageSize, Integer pageNumber, PlayerOrder order, String name, String title, Race race, Profession profession,
                                    Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                    Integer minLevel, Integer maxLevel) {

        return playerRepository.findAll()
                .stream()
                .filter(myPredicate(name,
                        title,
                        race,
                        profession,
                        after,
                        before,
                        banned,
                        minExperience,
                        maxExperience,
                        minLevel,
                        maxLevel))
                .collect(Collectors.toList());
    }


    public Player findOne(long id) {
        Optional<Player> foundPlayer = playerRepository.findById(id);
        return foundPlayer.orElse(null);
    }

    @Transactional
    public void save(Player player) {
        playerRepository.save(player);
    }

    @Transactional
    public void update(long id, Player updatePlayer) {

        updatePlayer.setId(id);
        playerRepository.save(updatePlayer);
    }

    @Transactional
    public void delete(long id) {
        playerRepository.deleteById(id);
    }


}
