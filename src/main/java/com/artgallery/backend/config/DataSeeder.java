package com.artgallery.backend.config;

import com.artgallery.backend.model.Artwork;
import com.artgallery.backend.model.Exhibition;
import com.artgallery.backend.model.User;
import com.artgallery.backend.repository.ArtworkRepository;
import com.artgallery.backend.repository.ExhibitionRepository;
import com.artgallery.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ExhibitionRepository exhibitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (artworkRepository.count() == 0) {
            Artwork a1 = new Artwork();
            a1.setTitle("Ethereal Whispers");
            a1.setArtist("Elena Vance");
            a1.setPrice(99000.0);
            a1.setHistory("Inspired by the misty mornings of the Scottish Highlands, this piece explores the thin line between reality and dreams. It represents the transient nature of memory.");
            a1.setMedium("Oil on Canvas");
            a1.setYear("2023");
            a1.setImage("https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?q=80&w=1000");
            a1.setStatus("approved");

            Artwork a2 = new Artwork();
            a2.setTitle("Vibrant Chaos");
            a2.setArtist("Julian Thorne");
            a2.setPrice(70000.0);
            a2.setHistory("A study of urban rhythm and color. Thorne painted this live in the heart of Montmartre, capturing the raw energy of modern street life mixed with classical techniques.");
            a2.setMedium("Acrylic and Mixed Media");
            a2.setYear("2024");
            a2.setImage("https://images.unsplash.com/photo-1541963463532-d68292c34b19?q=80&w=1000");
            a2.setStatus("approved");

            Artwork a3 = new Artwork();
            a3.setTitle("Solitude in Gold");
            a3.setArtist("Marcus Aurelius");
            a3.setPrice(265000.0);
            a3.setHistory("A neo-classical masterpiece following the Byzantine tradition of using gold leaf to represent divinity. It reflects the inner peace found in moments of silence.");
            a3.setMedium("Gold Leaf and Tempura");
            a3.setYear("2022");
            a3.setImage("https://images.unsplash.com/photo-1578301978693-85fa9c0320b9?q=80&w=1000");
            a3.setStatus("approved");

            Artwork a4 = new Artwork();
            a4.setTitle("Celestial Dance");
            a4.setArtist("Aria Sol");
            a4.setPrice(125000.0);
            a4.setHistory("A cosmic journey through swirls of stardust and nebulae. Aria uses a unique layering technique to create a sense of infinite depth and movement.");
            a4.setMedium("Acrylic on canvas");
            a4.setYear("2024");
            a4.setImage("https://images.unsplash.com/photo-1534796636912-3b95b3ab5986?q=80&w=1000");
            a4.setStatus("approved");

            Artwork a5 = new Artwork();
            a5.setTitle("Rustic Serenity");
            a5.setArtist("Thomas Miller");
            a5.setPrice(45000.0);
            a5.setHistory("Capturing the timeless beauty of a countryside sunset. This piece aims to transport the viewer to a place of quiet reflection and natural harmony.");
            a5.setMedium("Oil on Linen");
            a5.setYear("2023");
            a5.setImage("https://images.unsplash.com/photo-1500382017468-9049fed747ef?q=80&w=1000");
            a5.setStatus("approved");

            Artwork a6 = new Artwork();
            a6.setTitle("Inner Reflection");
            a6.setArtist("Zoe Chen");
            a6.setPrice(82000.0);
            a6.setHistory("A psychological portrait exploring the layers of the subconscious. Zoe uses reflection and transparency to reveal the hidden complexities of the soul.");
            a6.setMedium("Mixed media");
            a6.setYear("2024");
            a6.setImage("https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1000");
            a6.setStatus("approved");

            Artwork a7 = new Artwork();
            a7.setTitle("Urban Pulse");
            a7.setArtist("Dexter Volt");
            a7.setPrice(58000.0);
            a7.setHistory("The electric energy of the modern metropolis at dusk. Neon streaks and blurred figures capture the frantic yet beautiful pace of city life.");
            a7.setMedium("Digital painting printed on aluminum");
            a7.setYear("2024");
            a7.setImage("https://images.unsplash.com/photo-1514565131-fce0801e5785?q=80&w=1000");
            a7.setStatus("approved");

            Artwork a8 = new Artwork();
            a8.setTitle("Verdant Dreams");
            a8.setArtist("Luna Green");
            a8.setPrice(110000.0);
            a8.setHistory("A deep dive into the heart of an ancient forest. Luna's masterwork celebrates the raw power and intricate beauty of the natural world.");
            a8.setMedium("Oil on canvas");
            a8.setYear("2023");
            a8.setImage("https://images.unsplash.com/photo-1441974231531-c6227db76b6e?q=80&w=1000");
            a8.setStatus("approved");

            artworkRepository.save(a1);
            artworkRepository.save(a2);
            artworkRepository.save(a3);
            artworkRepository.save(a4);
            artworkRepository.save(a5);
            artworkRepository.save(a6);
            artworkRepository.save(a7);
            artworkRepository.save(a8);
        }

        if (exhibitionRepository.count() == 0) {
            Exhibition e1 = new Exhibition();
            e1.setTitle("Modern Echoes");
            e1.setDescription("Highlighting contemporary abstract masters.");
            e1.setCurator("Sarah Jenkins");

            exhibitionRepository.save(e1);
        }

        if (userRepository.findByEmail("manan@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("manan@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setName("Manan Shah");
            admin.setRole("admin");
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("curator@artvista.art").isEmpty()) {
            User curator = new User();
            curator.setEmail("curator@artvista.art");
            curator.setPassword(passwordEncoder.encode("curator123"));
            curator.setName("Sarah Jenkins");
            curator.setRole("curator");
            userRepository.save(curator);
        }

        if (userRepository.findByEmail("artist@artvista.art").isEmpty()) {
            User artist = new User();
            artist.setEmail("artist@artvista.art");
            artist.setPassword(passwordEncoder.encode("artist123"));
            artist.setName("Elena Vance");
            artist.setRole("artist");
            userRepository.save(artist);
        }
    }
}
