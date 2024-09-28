package com.deloitte.service; //package com.deloitte.service;
//
//import com.deloitte.domain.CsvFile;
//import com.deloitte.repository.CsvFileRepository;
//import com.deloitte.service.dto.CsvFileDTO;
//import com.deloitte.service.mapper.CsvFileMapper;
//import java.util.Optional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Service Implementation for managing {@link com.deloitte.domain.CsvFile}.
// */
//@Service
//@Transactional
//public class CsvFileService {
//
//    private static final Logger LOG = LoggerFactory.getLogger(CsvFileService.class);
//
//    private final CsvFileRepository csvFileRepository;
//
//    private final CsvFileMapper csvFileMapper;
//
//    public CsvFileService(CsvFileRepository csvFileRepository, CsvFileMapper csvFileMapper) {
//        this.csvFileRepository = csvFileRepository;
//        this.csvFileMapper = csvFileMapper;
//    }
//
//    /**
//     * Save a csvFile.
//     *
//     * @param csvFileDTO the entity to save.
//     * @return the persisted entity.
//     */
//    public CsvFileDTO save(CsvFileDTO csvFileDTO) {
//        LOG.debug("Request to save CsvFile : {}", csvFileDTO);
//        CsvFile csvFile = csvFileMapper.toEntity(csvFileDTO);
//        csvFile = csvFileRepository.save(csvFile);
//        return csvFileMapper.toDto(csvFile);
//    }
//
//    /**
//     * Update a csvFile.
//     *
//     * @param csvFileDTO the entity to save.
//     * @return the persisted entity.
//     */
//    public CsvFileDTO update(CsvFileDTO csvFileDTO) {
//        LOG.debug("Request to update CsvFile : {}", csvFileDTO);
//        CsvFile csvFile = csvFileMapper.toEntity(csvFileDTO);
//        csvFile = csvFileRepository.save(csvFile);
//        return csvFileMapper.toDto(csvFile);
//    }
//
//    /**
//     * Partially update a csvFile.
//     *
//     * @param csvFileDTO the entity to update partially.
//     * @return the persisted entity.
//     */
//    public Optional<CsvFileDTO> partialUpdate(CsvFileDTO csvFileDTO) {
//        LOG.debug("Request to partially update CsvFile : {}", csvFileDTO);
//
//        return csvFileRepository
//            .findById(csvFileDTO.getId())
//            .map(existingCsvFile -> {
//                csvFileMapper.partialUpdate(existingCsvFile, csvFileDTO);
//
//                return existingCsvFile;
//            })
//            .map(csvFileRepository::save)
//            .map(csvFileMapper::toDto);
//    }
//
//    /**
//     * Get all the csvFiles.
//     *
//     * @param pageable the pagination information.
//     * @return the list of entities.
//     */
//    @Transactional(readOnly = true)
//    public Page<CsvFileDTO> findAll(Pageable pageable) {
//        LOG.debug("Request to get all CsvFiles");
//        return csvFileRepository.findAll(pageable).map(csvFileMapper::toDto);
//    }
//
//    /**
//     * Get one csvFile by id.
//     *
//     * @param id the id of the entity.
//     * @return the entity.
//     */
//    @Transactional(readOnly = true)
//    public Optional<CsvFileDTO> findOne(Long id) {
//        LOG.debug("Request to get CsvFile : {}", id);
//        return csvFileRepository.findById(id).map(csvFileMapper::toDto);
//    }
//
//    /**
//     * Delete the csvFile by id.
//     *
//     * @param id the id of the entity.
//     */
//    public void delete(Long id) {
//        LOG.debug("Request to delete CsvFile : {}", id);
//        csvFileRepository.deleteById(id);
//    }
//}
