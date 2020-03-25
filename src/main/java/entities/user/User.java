package entities.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity(name = "User")
@Table(name = "User")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    public User(String name, String vorname, String userName, String hash, byte[] image) {
        this.name = name;
        this.vorname = vorname;
        this.userName = userName;
        this.hash = hash;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "vorname")
    private String vorname;

    @Column(name = "userName", unique = true)
    private String userName;

    @Column(name = "hash")
    private String hash;

    @Column(name = "profilePicture")
    private byte[] image;
}
