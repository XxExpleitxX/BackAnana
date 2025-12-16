package com.example.backAnana.Services.Impl;

import com.example.backAnana.Entities.Config;
import com.example.backAnana.Repositories.ConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigServiceImpl {

    private final ConfigRepository configRepository;

    public ConfigServiceImpl(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public double getDescuentoGlobal() {
        // buscamos el único registro de config (id=1)
        Optional<Config> configOpt = configRepository.findById(1L);
        return configOpt.map(Config::getDescuentoGlobal).orElse(0.0);
    }

    public void setDescuentoGlobal(double descuento) {
        Config config = configRepository.findById(1L).orElse(new Config(0.0));
        config.setDescuentoGlobal(descuento);
        configRepository.save(config);
    }

}
