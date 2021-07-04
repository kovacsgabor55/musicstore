package org.training360.musicstore;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MusicService {

    private final ModelMapper modelMapper;

    private final List<Instrument> instruments = new ArrayList<>(Collections.synchronizedList(new ArrayList<>()));

    private final AtomicLong idGenerator = new AtomicLong();

    public MusicService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<InstrumentDTO> searchInstrument(Optional<String> brand, Optional<Integer> price) {
        Type targetListType = new TypeToken<List<InstrumentDTO>>() {
        }.getType();

        List<Instrument> filtered = instruments.stream()
                .filter(e -> brand.isEmpty() || e.getBrand().equalsIgnoreCase(brand.get()))
                .filter(e -> price.isEmpty() || e.getPrice() == price.get())
                .collect(Collectors.toList());

        return modelMapper.map(filtered, targetListType);
    }

    public InstrumentDTO createInstrument(CreateInstrumentCommand command) {
        Instrument instrument = new Instrument(idGenerator.incrementAndGet(), command.getBrand(), command.getInstrumentType(), command.getPrice(), LocalDate.now());
        instruments.add(instrument);
        return modelMapper.map(instrument, InstrumentDTO.class);
    }

    public void deleteAllInstrument() {
        instruments.clear();
        idGenerator.set(0L);
    }

    public InstrumentDTO searchInstrumentById(long id) {
        return modelMapper.map(findById(id), InstrumentDTO.class);
    }

    public InstrumentDTO updateInstrumentPrice(long id, UpdatePriceCommand command) {
        Instrument instrument = findById(id);
        if (instrument.getPrice() != command.getPrice()) {
            instrument.setPrice(command.getPrice());
            instrument.setPostDate(LocalDate.now());
        }
        return modelMapper.map(instrument, InstrumentDTO.class);
    }

    public void deleteInstrumentById(long id) {
        Instrument instrument = instruments.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Instrument not found: " + id));
        instruments.remove(instrument);
    }

    private Instrument findById(long id) {
        return instruments.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Instrument not fount: " + id));
    }
}
