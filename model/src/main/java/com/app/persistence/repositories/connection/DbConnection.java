package com.app.persistence.repositories.connection;

import org.jdbi.v3.core.Jdbi;

// TODO zmienic z singleton
public class DbConnection {

    private final static DbConnection connection = new DbConnection();

    public static DbConnection getInstance() { return connection; }

    private DbConnection() {
        createTables();
    }

    private final String URL = "jdbc:mysql://localhost:3306/karol_wisniewski_jdbi_db?createDatabaseIfNotExist=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "root";

    private final Jdbi jdbi = Jdbi.create(URL, USERNAME, PASSWORD);

    public Jdbi getJdbi() { return jdbi; }

    private void createTables(){
        var USERS_TABLE_SQL = """
               create table if not exists users (
               id integer primary key auto_increment,
               name varchar(50) not null,
               surname varchar(50) not null,
               username varchar(50) not null,
               password varchar(50) not null,
               email varchar(50) not null,
               user_role ENUM('USER','ADMIN') not null
               );
               """;
        var TICKETS_TABLE_SQL = """
               create table if not exists tickets(
               id integer primary key auto_increment,
               user_id integer not null,
               seance_id integer not null,
               seat_id integer not null,
               price decimal(5, 2) not null,
               ticket_type ENUM('NORMAL', 'STUDENT', 'LESS_THAN_SIX_YEARS_OLD', 'MORE_THAN_SIXTY_YEARS_OLD') not null,
               status ENUM('FREE','RESERVATION', 'ORDERED', 'CANCELED'),
               foreign key (seance_id) references seances(id) on delete cascade on update cascade,
               foreign key (seat_id) references seats(id) on delete cascade on update cascade,
               foreign key (user_id) references users(id) on delete cascade on update cascade
               );
               """;
        var CINEMAS_TABLE_SQL = """
               create table if not exists cinemas(
               id integer primary key auto_increment,
               name varchar(50) not null,
               city varchar(50) not null
               );
                """;

        var MOVIES_TABLE_SQL = """
               create table if not exists movies(
               id integer primary key auto_increment,
               title varchar(50) not null,
               category ENUM('ACTION', 'HISTORY', 'COMEDY', 'CRIME', 'DRAMA', 'ADVENTURE', 'HORROR', 'MUSICAL', 'WAR', 'WESTERN', 'SCIENCE_FICTION'),
               display_since date not null,
               display_to date not null
               );
               """;

        var ROOMS_TABLE_SQL = """
                create table if not exists rooms(
                id integer primary key auto_increment,                
                name varchar(50) not null,
                rows_number integer not null,
                columns_number integer not null,
                cinema_id integer not null,
                foreign key (cinema_id) references cinemas(id) on delete cascade on update cascade 
                );
                """;

        var SEANCES_TABLE_SQL = """
                create table if not exists seances (
                id integer primary key auto_increment,
                movie_id integer not null,
                room_id integer not null,
                screening_date datetime not null,
                foreign key (movie_id) references movies(id) on delete cascade on update cascade   ,        
                foreign key (room_id) references rooms(id) on delete cascade on update cascade           
                );
                """;

        var SEATS_TABLE_SQL = """
                create table if not exists seats (
                id integer primary key auto_increment,
                room_id integer not null,
                roww integer not null,
                columnn integer not null,
                foreign key (room_id) references rooms(id) on delete cascade on update cascade
                );
                """;
        var FAVOURITES_TABLE_SQL = """
                create table if not exists favourites (
                id integer primary key auto_increment,
                user_id integer not null,
                movie_id integer not null,
                foreign key (user_id) references users(id) on delete cascade on update cascade,
                foreign key (movie_id) references movies(id) on delete cascade on update cascade
                );
                """;

        var TICKET_TYPE_TABLE_SQL = """
                create table if not exists tickets_type (
                id integer primary key auto_increment,
                name varchar(50) not null,
                base_price decimal(5, 2) not null,
                discount decimal(3, 2) not null
                );
                """;
        var INSERT_TICKETS_TYPES = """
                insert into tickets_type (name, base_price, discount)
                values
                ('NORMAL', 50.00, 0.00),
                ('STUDENT', 50.00, 0.50),
                ('LESS_THAN_SIX_YEARS_OLD', 50.00, 0.60),
                ('MORE_THAN_SIXTY_YEARS_OLD', 50.00, 0.70);
                """;

        var CREATE_VIEW = """
                create or replace view cinemas_with_counted_movies as       
                select c.name, c.city, m.title, m.category,  count(*) as ticket_counter
                from movies m
                join seances s on m.id = s.movie_id
                join tickets t on t.seance_id = s.id
                join rooms r on s.room_id = r.id
                join cinemas c on r.cinema_id = c.id
                group by c.id, m.id;
                """;
        
        jdbi
                .useHandle(handle -> {
                    handle.execute(USERS_TABLE_SQL);
                    handle.execute(CINEMAS_TABLE_SQL);
                    handle.execute(MOVIES_TABLE_SQL);
                    handle.execute(ROOMS_TABLE_SQL);
                    handle.execute(SEANCES_TABLE_SQL);
                    handle.execute(SEATS_TABLE_SQL);
                    handle.execute(FAVOURITES_TABLE_SQL);
                    handle.execute(TICKETS_TABLE_SQL);
                    handle.execute(CREATE_VIEW);
                    handle.execute(TICKET_TYPE_TABLE_SQL);
                    handle.execute(INSERT_TICKETS_TYPES);
                });

    }


}
