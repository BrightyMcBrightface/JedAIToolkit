/*
* Copyright [2016] [George Papadakis (gpapadis@yahoo.gr)]
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package EntityMatching;

import DataModel.AbstractBlock;
import DataModel.Attribute;
import DataModel.Comparison;
import DataModel.EntityProfile;
import DataModel.SimilarityPairs;
import Utilities.TextModels.AbstractModel;
import Utilities.TextModels.RepresentationModel;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.A.P. II
 */

public class ProfileMatcher extends AbstractEntityMatching {

    private static final Logger LOGGER = Logger.getLogger(ProfileMatcher.class.getName());
    
    private boolean isCleanCleanER; 
    
    protected AbstractModel[] entityModelsD1;
    protected AbstractModel[] entityModelsD2;
    protected RepresentationModel representationModel;
    
    public ProfileMatcher (RepresentationModel model) {
        representationModel = model;
        LOGGER.log(Level.INFO, "Initializing profile matcher with : {0}", model);
    }
    
    @Override
    public SimilarityPairs executeComparisons(List<AbstractBlock> blocks, 
            List<EntityProfile> profilesD1, List<EntityProfile> profilesD2) {
        if (profilesD1 == null) {
            LOGGER.log(Level.SEVERE, "First list of entity profiles is null! "
                    + "The first argument should always contain entities.");
            System.exit(-1);
        }
        
        boolean isCleanCleanER = false;
        entityModelsD1 = getModels(profilesD1);
        if (profilesD2 != null) {
            isCleanCleanER = true;
            entityModelsD2 = getModels(profilesD2);
        }
        
        SimilarityPairs simPairs = new SimilarityPairs(isCleanCleanER, blocks);
        for (AbstractBlock block : blocks) {
            final Iterator<Comparison> iterator = block.getComparisonIterator();
            while (iterator.hasNext()) {
                Comparison currentComparison = iterator.next();
                double sim = getSimilarity(currentComparison);
                currentComparison.setUtilityMeasure(sim);
                simPairs.addComparison(currentComparison);
            }
        }
        return simPairs;
    }
    
    private AbstractModel[] getModels(List<EntityProfile> profiles) {
        int counter = 0;
        AbstractModel[] models  = new AbstractModel[profiles.size()];
        for (EntityProfile profile : profiles) {
            models[counter] = RepresentationModel.getModel(representationModel, profile.getEntityUrl());
            for (Attribute attribute : profile.getAttributes()) {
                models[counter].updateModel(attribute.getValue());
            }
            counter++;
        }
        return models;
    }

    @Override
    public String getMethodInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMethodParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double getSimilarity(Comparison comparison) {
        if (isCleanCleanER) {
            return entityModelsD1[comparison.getEntityId1()].getSimilarity(entityModelsD2[comparison.getEntityId2()]);
        } 
            
        return entityModelsD1[comparison.getEntityId1()].getSimilarity(entityModelsD1[comparison.getEntityId2()]);
    }
}