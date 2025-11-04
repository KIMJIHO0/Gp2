package service;

import java.util.List;
import java.util.Optional;

import model.TravelPackage;
import model.enums.Region;
import model.enums.Theme;
import ports.PackageStore;

public class PackageServiceImpl implements PackageService {
    private final PackageStore packages;

    public PackageServiceImpl(PackageStore packages){
        this.packages = packages; }

    @Override public List<TravelPackage> listAll(){
        return packages.findAll(); }
    @Override public List<TravelPackage> search(Region r, Theme t, Integer max){
        return packages.search(r,t,max); }
    @Override public Optional<TravelPackage> detail(String id){
        return packages.findById(id); }
}
